(ns admin.interceptors
  (:require [re-frame.core :as rf]))

(def event-logger
  (rf/->interceptor
   :id :event-logger
   :before (fn [ctx]
             (->> ctx :coeffects (js/console.log "Before:"))
             ctx)

   :after (fn [ctx]
            (->> ctx :coeffects (js/console.log "After:"))
            ctx)))
