(ns admin.compositions.projects.events
  (:require [re-frame.core :as rf]
            [com.rpl.specter :as sp]
            [admin.routes :as routes]
            [admin.utils :refer [with-auth
                                 endpoint
                                 upload-request-body]]
            [ajax.core :refer [json-request-format
                               json-response-format]]))

;; --- GET Projects @ /api/projects
(rf/reg-event-fx
 :get-projects
 (fn [{:keys [db]} _]
   {:http-xhrio {:method          :get
                 :uri             (endpoint "projects")
                 :headers         (with-auth db)
                 :response-format (json-response-format {:keywords? true})
                 :on-success      [:get-projects-success]
                 :on-failure      [:api-request-error :get-projects]}}))

(rf/reg-event-db
 :get-projects-success
 (fn [db [_ {projects :data}]]
   (assoc db :projects projects)))

;; --- POST Projects @ /api/projects
(rf/reg-event-fx
 :create-project
 (fn [{:keys [db]} [_ values]]
   {:http-xhrio {:method          :post
                 :uri             (endpoint "projects")
                 :headers         (with-auth db)
                 :body            (upload-request-body values)
                 :response-format (json-response-format {:keywords? true})
                 :on-success      [:create-project-success]
                 :on-failure      [:api-request-error :create-project]}}))

(rf/reg-event-fx
 :create-project-success
 (fn [db [_ {project :data}]]
   {:redirect {:path (routes/project {:id (:id project)})}}))

;; --- DELETE Projects @ /api/projects/:id
(rf/reg-event-fx
 :delete-project
 (fn [{:keys [db]} [_ id]]
   {:http-xhrio {:method          :delete
                 :uri             (endpoint "projects" id)
                 :headers         (with-auth db)
                 :format          (json-request-format)
                 :response-format (json-response-format {:keywords? true})
                 :on-success      [:delete-project-success]
                 :on-failure      [:api-request-error :delete-project]}}))

(rf/reg-event-fx
 :delete-project-success
 (fn [{:keys [db] :as cofx}]
   (case (:active-page db)
     ;; when deleting a project from the projects list page
     ;; make sure to fetch projects and collections again
     :projects {:dispatch-n (list [:get-projects]
                                  [:get-gallery-collections])}
     ;; when deleting a project from project page,
     ;; redirect back to the projects list
     :project  {:redirect {:path (routes/projects)}}
     cofx)))

;; --- GET Project @ /api/galleries/collections
(rf/reg-event-fx
 :get-gallery-collections
 (fn [{:keys [db]}]
   {:http-xhrio {:method          :get
                 :uri             (endpoint "galleries/collections")
                 :headers         (with-auth db)
                 :response-format (json-response-format {:keywords? true})
                 :on-success      [:get-gallery-collections-success]
                 :on-failure      [:api-request-error]}}))

(rf/reg-event-db
 :get-gallery-collections-success
 (fn [db [_ {collections :data}]]
   ;; this event will always add collections to db
   (let [new-state                      (assoc db :gallery-collections collections)
         current-collection-items-count (->> collections
                                             (filter #(= (:active-gallery-name db) (:name %)))
                                             first
                                             :projects
                                             :items
                                             count)]
     ;; and at the same time, if :active-gallery-name is nil
     ;; or if the current gallery doesn't have any more
     ;; items left, then it will also set
     ;; an active gallery id
     (if (or (nil? (:active-gallery-name db))
             (not (pos? current-collection-items-count)))
       (assoc new-state :active-gallery-name (:name (first collections)))
       new-state))))

(rf/reg-event-db
 :set-active-gallery
 (fn [db [_ id]]
   (assoc db :active-gallery-name id)))
