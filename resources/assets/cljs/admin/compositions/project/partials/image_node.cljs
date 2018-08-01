(ns admin.compositions.project.partials.image-node
  (:require ["moment" :as moment]
            [reagent.core :as r]
            [re-frame.core :as rf]
            [cljss.core :refer-macros [defstyles]]
            [admin.components.project-image.view :as project-image]
            ))

(defstyles node-icon
  []
  {:color "#48B0F7"})

(defstyles thumbnail-wrapper
  []
  {:float         "right !important"
   :border-right  "1px solid #e6e6e6"
   :margin-right  "10px"
   :padding-right "15px"})

(defstyles mini-thumbnail
  []
  {:width "auto !important"})

(defn render
  [{:keys [node-item node-id on-update is-collapsed?]}]
  (rf/dispatch [:reset-form :edit-node (:id node-item) node-item])
  (let [form-state (rf/subscribe [:form-state :edit-node (:id node-item)])]
    (fn [{is-collapsed?          :is-collapsed?
         controls               :controls
         {caption    :caption
          src        :src
          created-at :created_at
          :as        node-item} :node-item}]
      [:<>
       [:div.card-header
        controls

        (when src
          [:div {:class (thumbnail-wrapper)}
           [:img {:height 30
                  :width  30
                  :class  (mini-thumbnail)
                  :src    (:src node-item)}]])

        [:div.user-pic
         [:i.fa.fa-camera {:class (node-icon)}]]

        [:h5 (or (:caption node-item) "Image node")]
        [:h6 "Created " (-> created-at :date moment .fromNow)]]

       (when-not is-collapsed?
         [:div.card-description
          [:div.row>div.col-12
           [:div.form-group
            [:label "Caption"]
            [:input.form-control
             {:type      :text
              :value     (:caption (:values @form-state))
              :on-blur   #(on-update (:values @form-state))
              :on-change #(rf/dispatch [:update-form-field
                                        [:edit-node (:id node-item)]
                                        :caption
                                        (-> % .-target .-value)])}]]]

          [:div.row>div.col-12
           [:div.form-group
            [:label "Image"]
            [project-image/render
             {:current-image src
              :on-update     (fn [[image]]
                               (rf/dispatch-sync [:update-form-field
                                                  [:edit-node (:id node-item)]
                                                  :src
                                                  image])
                               (on-update (:values @form-state)))
              :on-delete     #(rf/dispatch [:remove-node-image node-id])}]]]])])))
