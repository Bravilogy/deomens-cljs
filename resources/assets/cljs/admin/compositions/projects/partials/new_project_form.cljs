(ns admin.compositions.projects.partials.new-project-form
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [admin.utils :refer [slugify]]))

(defn render
  []
  (rf/dispatch-sync [:reset-form :new-project {}])
  (let [form-state (rf/subscribe [:form-state :new-project])]
    (fn []
      (let [{{title       :title
              slug        :slug
              description :description
              :as         values} :values
             is-valid?            :is-valid?} @form-state]
        [:div.card.card-default
         [:div.card-header
          [:div.card-title.p-b-5
           "New"
           [:div.small.hint-text "Create new project"]]]
         [:div.card-block
          [:div.form-group
           [:label "Title"]
           [:input.form-control
            {:type      :text
             :value     title
             :on-change (fn [e]
                          (let [val (-> e .-target .-value)]
                            (rf/dispatch [:update-form-field
                                          ;; which form?
                                          :new-project
                                          ;; which field?
                                          :title
                                          ;; new value
                                          val])

                            (rf/dispatch [:update-form-field
                                          :new-project
                                          :slug
                                          (slugify val)])))}]]

          [:div.form-group
           [:label "Slug"]
           [:input.form-control
            {:type      :text
             :value     slug
             :on-change (fn [e]
                          (rf/dispatch [:update-form-field
                                        :new-project
                                        :slug
                                        (slugify (-> e .-target .-value))]))}]]

          [:div.form-group
           [:label "Description"]
           [:span.help " (optional)"]
           [:input.form-control
            {:type      :text
             :value     description
             :on-change #(rf/dispatch [:update-form-field
                                       :new-project
                                       :description
                                       (-> % .-target .-value)])}]]

          [:form-group
           [:button.btn.btn-primary.btn-block
            {:disabled (empty? title)
             :on-click (fn [e]
                         (.preventDefault e)
                         (rf/dispatch [:create-project values]))}
            "Add"]]]]))))
