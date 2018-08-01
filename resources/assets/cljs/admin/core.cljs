(ns admin.core
  (:require [admin.subs]
            [admin.events]
            [admin.view :as view]
            [admin.config :as config]
            [admin.routes :as routes]
            [reagent.core :as reagent]
            [re-frame.core :as re-frame]
            ))

(defn dev-setup []
  (when config/debug?
    (enable-console-print!)
    (println "working in dev mode")))

(defn mount-root []
  (re-frame/clear-subscription-cache!)
  (reagent/render [view/application]
                  (.getElementById js/document "application")))

(defn ^:export init []
  (dev-setup)
  (routes/app-routes)
  (re-frame/dispatch-sync [:initialize-application])
  (mount-root))
