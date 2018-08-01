(ns admin.compositions.project.partials.form-progress
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [admin.utils :refer [slugify]]))

(defn- element-by-id
  "shorthand of .getElementById"
  [id]
  (.getElementById js/document id))

(defn- is-in-viewport?
  "check if an element in in viewport"
  [selector]
  (when-let [top (some-> (element-by-id selector)
                         .getBoundingClientRect
                         .-top)]
    (< top 450)))

(defn- item->selector
  "this helper function will slugify andtransform an
  item i.e. Title to a seletor i.e. form-item-title"
  [item]
  (str "form-item-" (slugify item)))

(defn- get-active-item
  [form-items scroll-top]
  (or (->> form-items
           (filter (comp is-in-viewport? item->selector))
           last)
      (first form-items)))

(defn render
  [form-items]
  (let [container        (r/atom nil)
        floating-element (r/atom nil)
        scroll-top       (rf/subscribe [:scroll-top])
        floating-top     (rf/subscribe [:floating-top
                                        {:container        container
                                         :floating-element floating-element}])]
    (fn [form-items]
      (let [items       (mapv vector form-items (iterate inc 1))
            active-item (get-active-item form-items @scroll-top)]
        [:div.container
         {:ref #(reset! container %)}
         [:div.project__form-progress
          {:ref   #(reset! floating-element %)
           :style {:top (str @floating-top "px")} }
          [:ul.list-unstyled
           (for [[item index] items]
             ^{:key item}
             [:li {:class (when (= item active-item) "active")}
              [:div.step-number index]
              [:div.step-title item]])]]]))))
