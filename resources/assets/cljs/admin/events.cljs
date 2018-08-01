(ns admin.events
  (:require ["sweetalert2" :as sw]
            ["react-toastify" :as toastify]
            ["react-confirm-alert" :as confirm]
            [re-frame.core :as rf]
            [clojure.string :as s]
            [day8.re-frame.http-fx]
            [admin.routes :as routes]
            [admin.db :refer [default-db]]
            [admin.utils :refer [with-auth
                                 endpoint]]
            [ajax.core :refer [json-request-format
                               json-response-format]]
            [secretary.core :refer [dispatch!]]
            [admin.compositions.project.events]
            [admin.compositions.projects.events]
            [admin.components.collection.events]
            [admin.compositions.galleries.events]
            [admin.compositions.dashboard.events]
            [admin.interceptors :refer [event-logger]]))

(def quotes-sources [{:uri             "http://api.icndb.com/jokes/random"
                      :quote-formatter (fn [quote]
                                         {:author  "Chuck Norris"
                                          :content (get-in quote [:value :joke])})}

                     {:uri             "http://quotes.stormconsultancy.co.uk/random.json"
                      :quote-formatter (fn [{author  :author
                                            content :quote}]
                                         {:author  author
                                          :content content})}])

(defn set-hash!
  "Set the location hash of a js/window object."
  ([v] (set-hash! (.-location js/window) v))
  ([loc v] (aset loc "hash" v)))

;; -----------------------------
;; Effects
;; -----------------------------
(rf/reg-fx
 :redirect
 (fn [{:keys [url path]}]
   (when url
     (.replace (.-location js/window) url))
   (when path
     (set-hash! path))))

(rf/reg-fx
 :notification
 (fn [{message :message
      type    :type
      :or     {type :info}}]
   (toastify/toast
    message
    (clj->js {:type        (name type)
              :autoClose   3000
              :closeButton false}))))

(rf/reg-fx
 :confirm
 (fn [options]
   (confirm/confirmAlert (clj->js options))))

;; -----------------------------
;; Event handlers
;; -----------------------------
(rf/reg-event-db
 :save-scroll-top
 (fn [db [_ top]]
   (assoc-in db [:ui :scroll-top] top)))

(rf/reg-event-fx
 :confirm
 (fn [cofx [_ {:keys [title message on-confirm on-cancel]}]]
   {:confirm {:title   title
              :message message
              :buttons [{:label   "Yes"
                         :onClick on-confirm}
                        {:label   "No"
                         :onClick on-cancel}]}}))

(rf/reg-event-fx
 :initialize-application
 [(rf/inject-cofx :client-token-meta)]
 (fn [cofx _]
   (let [token (:csrf_token cofx)]
     {:db       (assoc-in default-db [:client :csrf_token] token)
      :dispatch [:get-user]})))

(rf/reg-event-fx
 :set-active-page
 (fn [{:keys [db]} [_ {:keys [page id]}]]
   (let [set-page (assoc db
                         :active-page page
                         :forms nil)]
     (case page

       :dashboard {:db         (assoc set-page
                                      :page-title "Dashboard"
                                      :breadcrumb [{:label "Dashboard"}])
                   :dispatch-n (list [:get-dashboard]
                                     [:get-random-quote])}

       :projects {:db         (assoc set-page
                                     :page-title "Projects"
                                     :breadcrumb [{:label "Projects"}])
                  :dispatch-n (list [:get-projects]
                                    [:get-gallery-collections])}

       :project {:db         set-page
                 :dispatch-n (list [:get-galleries]
                                   [:get-project id])}


       :galleries {:db         (assoc set-page
                                      :page-title "Galleries"
                                      :breadcrumb [{:label "Galleries"}])
                   :dispatch-n (list [:get-galleries])}

       ))))

;; --- GET User @ /api/user
(rf/reg-event-fx
 :get-user
 (fn [{:keys [db]} _]
   {:http-xhrio {:method          :get
                 :uri             (endpoint "user")
                 :headers         (with-auth db)
                 :response-format (json-response-format {:keywords? true})
                 :on-success      [:get-user-success]
                 :on-failure      [:api-request-error :get-user]}
    :db         (assoc-in db      [:loading :user] true)}))

(rf/reg-event-db
 :get-user-success
 (fn [db [_ user]]
   (-> db
       (assoc-in [:loading :user] false)
       (assoc :user user))))

;; --- POST User Logout @ /api/user
(rf/reg-event-fx
 :logout-user
 (fn [{:keys [db]} _]
   {:http-xhrio {:method          :post
                 :uri             (endpoint "logout")
                 :headers         (with-auth db)
                 :format          (json-request-format)
                 :response-format (json-response-format {:keywords? true})
                 :on-success      [:user-logout-success]
                 :on-failure      [:api-request-error :user-logout]}}))

(rf/reg-event-fx
 :user-logout-success
 (fn [cofx _]
   {:redirect {:url "/admin"}}))

;; --- GET Quotes @ Chuck Norris & Programming
(rf/reg-event-fx
 :get-random-quote
 (fn [cofx _]
   (let [{:keys [uri quote-formatter]} (rand-nth quotes-sources)]
     {:http-xhrio {:method          :get
                   :uri             uri
                   :response-format (json-response-format {:keywords? true})
                   :on-success      [:get-random-quote-success quote-formatter]
                   :on-failure      [:api-request-error :get-random-quote]}})))

(rf/reg-event-db
 :get-random-quote-success
 (fn [db [_ formatter-fn quote]]
   (assoc-in db [:dashboard :quote] (formatter-fn quote))))

;; form handlers
(rf/reg-event-db
 :reset-form
 (fn [db [_ & args]]
   (let  [[which-form? defaults] ((juxt pop peek) (vec args))]
     (assoc-in db (concat [:forms] which-form?) {:values    defaults
                                                 :is-valid? true}))))

(rf/reg-event-db
 :update-form-field
 (fn [db [_ form field value]]
   (let [field-path (concat
                     [:forms]
                     (if (vector? form) form [form])
                     [:values field])]
     (assoc-in db field-path value))))

;; --- Handle errors
(rf/reg-event-fx
 :api-request-error
 (fn [{:keys [db]} [_ request-type response]]
   {:db           (assoc-in db [:error request-type] "There was an error")
    :notification {:message (or (:message response)
                                (str request-type " failed"))
                   :type    "error"}}))
