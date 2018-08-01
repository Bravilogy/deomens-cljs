(ns admin.compositions.dashboard.events
  (:require [re-frame.core :as rf]
            [admin.utils :refer [endpoint
                                 with-auth]]
            [ajax.core :refer [json-request-format
                               json-response-format
                               transit-request-format]]))

;; --- GET Dashboard @ /api/dashboard
(rf/reg-event-fx
 :get-dashboard
 (fn [{:keys [db]} _]
   {:http-xhrio {:method          :get
                 :uri             (endpoint "dashboard")
                 :headers         (with-auth db)
                 :response-format (json-response-format {:keywords? true})
                 :on-success      [:get-dashboard-success]
                 :on-failure      [:api-request-error :get-dashboard]}
    :db         (assoc-in db     [:loading :dashboard] true)}))

(rf/reg-event-db
 :get-dashboard-success
 (fn [db [_ dashboard-data]]
   (-> db
       (assoc-in [:loading :dashboard] false)
       (assoc :dashboard (merge (:dashboard db) dashboard-data)))))
