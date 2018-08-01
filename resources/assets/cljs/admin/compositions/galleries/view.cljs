(ns admin.compositions.galleries.view
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [admin.components.loading.view :as loading]
            [admin.compositions.galleries.partials.new-gallery-form :as new-gallery-form]
            ))

(defn- gallery-item
  [gallery]
  (rf/dispatch [:reset-form :gallery-item (:id gallery) gallery])
  (let [form-state       (rf/subscribe [:form-state :gallery-item (:id gallery)])
        is-edit-mode?    (r/atom false)
        edit-mode-toggle (fn [e]
                           (.preventDefault e)
                           (reset! is-edit-mode? (not @is-edit-mode?)))]

    (fn [gallery]
      (let [{{name :name
              slug :uri
              :as  values} :values} @form-state]
        [:li.gallery-list__gallery-item
         [:div.pull-right
          [:ul.list-inline.no-margin
           [:li
            (if (not @is-edit-mode?)
              [:a {:href     "#"
                   :on-click edit-mode-toggle}
               [:i.pg.pg-plus]]
              [:a {:href     "#"
                   :on-click edit-mode-toggle}
               [:i.pg.pg-minus]])]

           [:li
            [:a {:href     "#"
                 :on-click (fn [e]
                             (.preventDefault e)
                             (rf/dispatch [:delete-gallery (:id gallery)]))}
             [:i.fa.fa-trash]]]]]

         (if (not @is-edit-mode?)
           [:h5 name]
           [:div.form-group
            [:label "Name"]
            [:input.form-control
             {:value     name
              :on-blur   #(when (not= values gallery)
                            (rf/dispatch [:update-gallery values]))
              :on-change #(rf/dispatch [:update-form-field
                                        [:gallery-item (:id gallery)]
                                        :name
                                        (-> % .-target .-value)])}]])

         (if (not @is-edit-mode?)
           [:h6 (str "/" slug)]
           [:div.form-group
            [:label "URL"]
            [:input.form-control
             {:value     slug
              :on-blur   #(when (not= values gallery)
                            (rf/dispatch [:update-gallery values]))
              :on-change #(rf/dispatch [:update-form-field
                                        [:gallery-item (:id gallery)]
                                        :uri
                                        (-> % .-target .-value)])}]])]))))

(defn- main-panel
  [galleries]
  [:div.container.no-padding>div.content-container>div.row
   [:div.col-4
    [new-gallery-form/render]]

   [:div.col-8
    [:div.card.card-default
     [:div.card-header
      [:div.card-title.p-b-5
       "Galleries list"]]
     [:div.card-block
      [:ul.gallery-list.list-unstyled
       (for [gallery galleries]
         ^{:key (:id gallery)}
         [gallery-item gallery])]]]]])

(defn render
  []
  (let [is-loading? (rf/subscribe [:is-loading? :galleries])
        galleries   (rf/subscribe [:galleries])]
    (fn []
      (if @is-loading?
        [loading/render]
        [main-panel @galleries]))))
