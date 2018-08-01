(ns admin.routes
  (:import goog.History)
  (:require [secretary.core :as secretary :refer-macros [defroute]]
            [goog.events :as gevents]
            [goog.history.EventType :as EventType]
            [re-frame.core :as re-frame]))

(secretary/set-config! :prefix "#")

(defn hook-browser-navigation! []
  (doto (History.)
    (gevents/listen
     EventType/NAVIGATE
     (fn [event]
       (secretary/dispatch! (.-token event))))
    (.setEnabled true)))

(defn app-routes []
  (hook-browser-navigation!))

;; ---------------------------------
;; having routes outside of function
;; allows us to have named routes
;; for future use purposes
;; ---------------------------------
(defroute dashboard "/" []
  (re-frame/dispatch [:set-active-page {:page :dashboard}]))

(defroute projects "/projects" []
  (re-frame/dispatch [:set-active-page {:page :projects}]))

(defroute project "/projects/:id" [id]
  (re-frame/dispatch [:set-active-page {:id   id
                                        :page :project}]))

(defroute galleries "/galleries" []
  (re-frame/dispatch [:set-active-page {:page :galleries}]))
