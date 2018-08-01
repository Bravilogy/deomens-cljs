(ns admin.compositions.project.partials.form-item
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [admin.utils :refer [slugify]]
            [cljss.core :refer-macros [defstyles]]))

(defstyles title-styles
  []
  {:cursor "pointer"})

(defn view
  "helper function to generate form cards HTML"
  [options-or-title]
  (let [title             (get options-or-title :title options-or-title)
        sub-title         (get options-or-title :sub-title)
        collapsable?      (get options-or-title :collapsable? false)
        is-collapsed?     (r/atom (get options-or-title :is-collapsed?))
        show-in-progress? (get options-or-title :show-in-progress? true)
        collapse-handler  (fn [e]
                            (.preventDefault e)
                            (reset! is-collapsed? (not @is-collapsed?)))]
    (when show-in-progress?
      (rf/dispatch [:save-form-item-title title]))
    (fn [_ & children]
      [:div.row.form-items {:id (str "form-item-" (slugify title))}
       [:div.col-12
        [:div.card.card-default
         [:div.card-header
          [:div.card-title.p-b-5
           {:on-click (when collapsable? collapse-handler)
            :class    (when collapsable? (title-styles))}
           title
           (when sub-title
             [:div.small.hint-text sub-title])]
          (when collapsable?
            [:div.card-controls
             [:ul
              [:li
               [:a {:href "#"}
                [:i {:class    (if @is-collapsed? :pg-arrow_minimize :pg-arrow_maximize)
                     :on-click (fn [e]
                                 (.preventDefault e)
                                 (reset! is-collapsed? (not @is-collapsed?)))}]]]]])]
         (when (not @is-collapsed?)
           (into [:div.card-block] children))]]])))
