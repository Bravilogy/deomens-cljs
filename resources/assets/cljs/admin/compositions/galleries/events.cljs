(ns admin.compositions.galleries.events
  (:require [re-frame.core :as rf]
            [com.rpl.specter :as sp]
            [ajax.core :refer [json-request-format
                               json-response-format]]
            [admin.utils :refer [with-auth
                                 upload-request-body
                                 endpoint]]))

;; --- GET Galleies @ /api/galleries
(rf/reg-event-fx
 :get-galleries
 (fn [{:keys [db]} [_ id]]
   {:http-xhrio {:method          :get
                 :uri             (endpoint "galleries")
                 :headers         (with-auth db)
                 :response-format (json-response-format {:keywords? true})
                 :on-success      [:get-galleries-success]
                 :on-failure      [:api-request-error :get-galleries]}
    :db         (assoc-in db      [:loading :galleries] true)}))

(rf/reg-event-db
 :get-galleries-success
 (fn [db [_ {galleries :data}]]
   (-> db
       (assoc-in [:loading :galleries] false)
       (assoc :galleries galleries))))

;; --- PUT Gallery @ /api/galleries/:id
(rf/reg-event-fx
 :update-gallery
 (fn [{:keys [db]} [_ gallery]]
   {:db         (assoc-in db      [:forms :update-gallery (:id gallery) :is-updating?] true)
    :http-xhrio {:method          :put
                 :uri             (endpoint "galleries" (:id gallery))
                 :params          gallery
                 :headers         (with-auth db)
                 :format          (json-request-format)
                 :response-format (json-response-format {:keywords? true})
                 :on-success      [:update-gallery-success]
                 :on-failure      [:api-request-error :update-gallery]}}))

(rf/reg-event-fx
 :update-gallery-success
 (fn [{:keys [db]} [_ {gallery :data}]]
   (let [db (assoc-in db [:forms :update-gallery (:id gallery) :is-updating?] false)])
   {:db           (sp/setval [:galleries sp/ALL #(= (:id %) (:id gallery))] gallery db)
    :notification {:message "Gallery updated successfully"}}))

;; --- POST Galleries @ /api/galleries
(rf/reg-event-fx
 :create-gallery
 (fn [{:keys [db]} [_ values]]
   {:http-xhrio {:method          :post
                 :uri             (endpoint "galleries")
                 :headers         (with-auth db)
                 :body            (upload-request-body values)
                 :response-format (json-response-format {:keywords? true})
                 :on-success      [:create-gallery-success]
                 :on-failure      [:api-request-error :create-project]}}))

(rf/reg-event-fx
 :create-gallery-success
 (fn [{:keys [db]} [_ {gallery :data}]]
   {:notification {:message (str "New gallery added - " (:name gallery))}
    :db           (update db :galleries #(cons gallery %))}))

;; --- DELETE Gallery @ /api/galleries/:id
(rf/reg-event-fx
 :delete-gallery
 (fn [{:keys [db]} [_ gallery-id]]
   {:http-xhrio {:method          :delete
                 :uri             (endpoint "galleries" gallery-id)
                 :headers         (with-auth db)
                 :response-format (json-response-format {:keywords? true})
                 :format          (json-request-format)
                 :on-success      [:delete-gallery-success gallery-id]
                 :on-failure      [:api-request-error :delete-gallery]}}))

(rf/reg-event-db
 :delete-gallery-success
 (fn [db [_ gallery-id]]
   (sp/setval [:galleries sp/ALL #(= (:id %) gallery-id)] sp/NONE db)))
