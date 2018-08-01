(ns admin.compositions.project.events
  (:require [re-frame.core :as rf]
            [com.rpl.specter :as sp]
            [admin.routes :as routes]
            [ajax.core :refer [json-request-format
                               transit-request-format
                               json-response-format]]
            [admin.utils :refer [with-auth
                                 endpoint
                                 upload-request-body]]))

(rf/reg-event-db
 :save-form-item-title
 (fn [db [_ title]]
   (let [titles (get db :project-form-items #{})]
     (assoc db :project-form-items (conj titles title)))))

;; --- GET Project @ /api/projects/:id
(rf/reg-event-fx
 :get-project
 (fn [{:keys [db]} [_ id]]
   {:http-xhrio {:method          :get
                 :uri             (endpoint "projects" id)
                 :headers         (with-auth db)
                 :response-format (json-response-format {:keywords? true})
                 :on-success      [:get-project-success]
                 :on-failure      [:api-request-error :get-project]}
    :db         (assoc-in db      [:loading :project] true)}))

(rf/reg-event-db
 :get-project-success
 (fn [db [_ {project :data}]]
   (-> db
       (assoc-in [:loading :project] false)
       (assoc :active-project project
              :page-title (:title project)
              :breadcrumb [{:label "Projects"
                            :path  (routes/projects)}
                           {:label (:title project)}]))))

;; --- PUT Project Update @ /api/projects/:id
(rf/reg-event-fx
 :update-project
 (fn [{:keys [db]} [_ project]]
   {:db         (assoc-in db      [:forms :update-project :is-updating?] true)
    :http-xhrio {:method          :put
                 :uri             (endpoint "projects" (:id project))
                 :params          project
                 :headers         (with-auth db)
                 :format          (json-request-format)
                 :response-format (json-response-format {:keywords? true})
                 :on-success      [:update-project-success]
                 :on-failure      [:api-request-error :update-project]}}))

(rf/reg-event-fx
 :update-project-success
 (fn [{:keys [db]} [_ {project :data}]]
   {:db           (assoc db :active-project project)
    :dispatch     [:reset-form :update-project project]
    :notification {:message "Project updated successfully"}}))

;; --- POST Project Image @ /api/projects/images
(rf/reg-event-fx
 :upload-project-image
 (fn [{:keys [db]} [_ {project-id :project-id
                      image-type :image-type
                      [image]    :images}]]
   (let [request-body (upload-request-body {:id   project-id
                                            :type image-type
                                            :file image})]
     {:http-xhrio {:method          :post
                   :uri             (endpoint "projects/images")
                   :headers         (with-auth db)
                   :body            request-body
                   :response-format (json-response-format {:keywords? true})
                   :on-success      [:update-active-project]
                   :on-failure      [:api-request-error :upload-project-image]}})))

;; --- DELETE Project Image @ /api/projects/images
(rf/reg-event-fx
 :delete-project-image
 (fn [{:keys [db]} [_ {:keys [project-id image-type]}]]
   {:http-xhrio {:method          :delete
                 :uri             (endpoint "projects/images" project-id)
                 :headers         (with-auth db)
                 :params          {:type image-type}
                 :response-format (json-response-format {:keywords? true})
                 :format          (json-request-format)
                 :on-success      [:update-active-project]
                 :on-failure      [:api-request-error :delete-project-image]}}))

(rf/reg-event-db
 :update-active-project
 (fn [db [_ {project :data}]]
   (-> db
       (assoc :active-project project))))


;; --- POST Project Node @ /api/projects/nodes
(rf/reg-event-fx
 :create-media-node
 (fn [{:keys [db]} [_ node-type project-id]]
   {:http-xhrio {:method          :post
                 :uri             (endpoint "projects/nodes")
                 :headers         (with-auth db)
                 :params          {:type node-type
                                   :id   project-id}
                 :format          (json-request-format)
                 :response-format (json-response-format {:keywords? true})
                 :on-success      [:create-media-node-success]
                 :on-failure      [:api-request-error :create-media-node]}}))

(rf/reg-event-db
 :create-media-node-success
 (fn [db [_ {media-nodes :data}]]
   (let [media-nodes (sp/setval [:items sp/LAST :is-collapsed?] false media-nodes)
         new-db      (sp/setval [:active-project :mediaNodes] media-nodes db)]
     new-db)))


;; --- DELETE Project Node @ /api/projects/nodes/:id
(rf/reg-event-fx
 :delete-project-node
 (fn [{:keys [db]} [_ id]]
   {:http-xhrio {:method          :delete
                 :uri             (endpoint "projects/nodes" id)
                 :headers         (with-auth db)
                 :format          (json-request-format)
                 :response-format (json-response-format {:keywords? true})
                 :on-success      [:delete-project-node-success]
                 :on-failure      [:api-request-error :delete-project-node]}}))

(rf/reg-event-db
 :delete-project-node-success
 (fn [db [_ {media-nodes :data}]]
   (assoc-in db [:active-project :mediaNodes] media-nodes)))

;; --- PUT Project Media Node @ /api/projects/nodes
(rf/reg-event-fx
 :update-media-node
 (fn [{:keys [db]} [_ node-id data]]
   (let [request-body (upload-request-body data)]
     ;; spoof the PUT request so that
     ;; file upload works as well
     (.append request-body "_method" "PUT")
     {:http-xhrio {:method          :post
                   :uri             (endpoint "projects/nodes" node-id)
                   :headers         (with-auth db)
                   :body            request-body
                   :response-format (json-response-format {:keywords? true})
                   :format          (transit-request-format)
                   :on-success      [:update-media-node-success]
                   :on-failure      [:api-request-error :update-media-node]}})))

(rf/reg-event-db
 :update-media-node-success
 (fn [db [_ {node :data}]]
   (sp/setval [:active-project
               :mediaNodes
               :items
               sp/ALL
               #(= (:id %)
                   (:id node))] node db)))


;; --- DELETE Project Media Node Image @ /api/projects/nodes/:id/image
(rf/reg-event-fx
 :remove-node-image
 (fn [{:keys [db]} [_ node-id]]
   {:http-xhrio {:method          :delete
                 :uri             (endpoint "projects/nodes" node-id "image")
                 :headers         (with-auth db)
                 :response-format (json-response-format {:keywords? true})
                 :format          (json-request-format)
                 :on-success      [:update-media-node-success]
                 :on-failure      [:api-request-error :remove-node-image]}}))
