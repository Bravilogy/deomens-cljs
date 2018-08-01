(ns admin.compositions.galleries.partials.new-gallery-form
  (:require [re-frame.core :as rf]
            [admin.utils :refer [slugify]]))

(defn- update-field
  ([field-name] (update-field field-name identity))
  ([field-name transformer]
   (fn [e]
     (rf/dispatch
      [:update-form-field
       :new-gallery
       field-name
       (-> e .-target .-value transformer)]))))

(defn render
  []
  (rf/dispatch-sync [:reset-form :new-gallery {}])
  (let [form-state (rf/subscribe [:form-state :new-gallery])]
    (fn []
      (let [{{name :name
              slug :uri
              :as  values} :values} @form-state]
        [:div.card.card-default
         [:div.card-header
          [:div.card-title.p-b-5
           "New"
           [:div.small.hint-text "Create new gallery"]]]
         [:div.card-block
          [:div.form-group
           [:label "Gallery name"]
           [:input.form-control
            {:type      :text
             :on-change (update-field :name)
             :value     name}]]
          [:div.form-group
           [:label "Gallery slug"]
           [:input.form-control
            {:type      :text
             :on-change (update-field :uri slugify)
             :value     slug}]]

          [:form-group
           [:button.btn.btn-primary.btn-block
            {:disabled (empty? name)
             :on-click (fn [e]
                         (.preventDefault e)
                         (rf/dispatch [:create-gallery values]))}
            "Add"]]]]))))
