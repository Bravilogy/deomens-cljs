(ns admin.components.collection.events
  (:require [re-frame.core :as rf]
            [com.rpl.specter :as sp]
            [admin.utils :refer [with-auth
                                 endpoint
                                 create-converter]]
            [ajax.core :refer [json-request-format
                               json-response-format]]))

(rf/reg-event-db
 :update-collection-order
 (fn [db [_ path new-order]]
   (sp/setval path new-order db)))

;; --- POST Collections @ /api/collections
(rf/reg-event-fx
 :sync-collection-order
 (fn [{:keys [db]} [_ collection-id path new-order]]
   {:http-xhrio {:method          :post
                 :uri             (endpoint "collections")
                 :headers         (with-auth db)
                 :params          {:id    collection-id
                                   :order new-order}
                 :response-format (json-response-format {:keywords? true})
                 :format          (json-request-format)
                 :on-success      [:sync-collection-order-success collection-id new-order]
                 :on-failure      [:api-request-error :reorder-list]}
    :db         (->> db
                     (sp/setval [:loading :collections collection-id] true)
                     (sp/setval path new-order))}))

(rf/reg-event-db
 :sync-collection-order-success
 (fn [db [_ collection-id]]
   (assoc-in db [:loading :collections collection-id] false)))

