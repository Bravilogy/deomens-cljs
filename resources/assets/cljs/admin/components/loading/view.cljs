(ns admin.components.loading.view
  (:require [cljss.core :refer-macros [defstyles]]))

(defstyles loading-bar
  []
  {:margin "270px auto 0 auto"
   :width  "150px"})

(defn render
  []
  [:div.col-lg-12.text-center
   [:div.progress.progress-small
    {:class (loading-bar)}
    [:div.progress-bar-indeterminate]]])
