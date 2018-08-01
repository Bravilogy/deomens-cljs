(ns admin.compositions.dashboard.subs
  (:require [re-frame.core :as rf]))

(defn dashboard-data [db]
  (:dashboard db))

(rf/reg-sub :dashboard-data dashboard-data)
