(ns admin.compositions.project.partials.quick-info
  (:require [cljss.core :refer-macros [defstyles]]))

(defstyles quick-info-dt
  []
  {:float "left"
   :width "100px"})

(defstyles quick-info-dd
  []
  {:margin-bottom "0"})

(defn- quick-info-item
  [[label value]])

(defn- thing-renderer
  [[label value]]
  [:<>
   {:key label}
   [:dt {:class (quick-info-dt)} [:small.bold label]]
   [:dd {:class (quick-info-dd)} [:small value]]]
  )

(defn render
  "a helper function to render
  quick info items at the top"
  [things]
  [:dl
   (->> things
        (filter (comp not-empty str last))
        (map thing-renderer))])
