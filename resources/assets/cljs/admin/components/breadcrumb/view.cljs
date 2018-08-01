(ns admin.components.breadcrumb.view
  (:require [admin.routes :refer [dashboard]]))

(defn breadcrumb-item
  [{:keys [path label]} active?]
  [:li.breadcrumb-item {:class (when active? "active")
                        :style (when active? {:margin-left 0
                                              :font-size "inherit"})
                        :key label}
   (if active?
     label
     [:a {:href path} label])])

(defn render [items]
  [:div {:data-pages :parallax}
   [:div.container.no-padding.container-fixed-lg
    [:div {:class :inner
           :style {:transform "translateY(0px)"
                   :opacity 1}}
     [:ol.breadcrumb
      [:li.breadcrumb-item
       [:a {:href (dashboard)} "Home"]]
      (map #(breadcrumb-item % (= % (last items))) items)]]]])
