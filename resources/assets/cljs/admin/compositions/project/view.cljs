(ns admin.compositions.project.view
  (:require ["moment" :as moment]
            ["react-day-picker" :as dp]
            ["react-dropzone" :as drop-zone]
            ["react-tagsinput" :as tagsinput]
            ["react-confirm-alert" :as confirm]
            [reagent.core :as r]
            [re-frame.core :as rf]
            [clojure.string :as s]
            [admin.utils :refer [to-timestamp
                                 unix-from-now]]
            [admin.components.loading.view :as loading]
            [admin.components.project-image.view :as project-image]
            [admin.components.markdown-editor.view :as markdown-editor]
            [admin.compositions.project.partials.form-item :as form-item]
            [admin.compositions.project.partials.quick-info :as quick-info]
            [admin.compositions.project.helpers :refer [title-blur-handler
                                                        project-image-update-handler
                                                        project-image-delete-handler
                                                        suggested-slug]]
            [admin.compositions.project.partials.form-progress :as form-progress]
            [admin.compositions.project.partials.media-nodes-list :as media-nodes-list]
            [admin.compositions.project.partials.media-node-buttons :as media-node-buttons]
            ))

(defn main-panel
  "we store project props as defaults in the app state
  and we populate fields from the form subscription
  once all changes are saved, the form will be
  reset and the defaults will be updated."
  [project]
  (rf/dispatch-sync
   [:reset-form
    :update-project
    (select-keys project [:id
                          :title
                          :slug
                          :tags
                          :description
                          :featured
                          :published
                          :gallery_id
                          :published_at])])
  (let [form-state              (rf/subscribe [:form-state :update-project])
        galleries               (rf/subscribe [:galleries])
        on-project-image-update (partial project-image-update-handler (:id project))
        on-project-image-delete (partial project-image-delete-handler (:id project))]
    (fn [defaults form-items]
      (let [{{title         :title
              slug          :slug
              description   :description
              is-featured?  :featured
              is-published? :published
              gallery-id    :gallery_id
              tags          :tags
              published-at  :published_at} :values
             is-valid?                     :is-valid?
             is-updating?                  :is-updating?} @form-state
            images                                        (:images defaults)
            media-nodes                                   (:mediaNodes defaults)]
        [:div.container.no-padding>div.content-container>div.row
         [:div.col-12
          [:div.card.card-default
           [:div.card-block
            [:h4 "Editing project: " (:title defaults)]
            [quick-info/render {"id"          (:id defaults)
                                "slug"        (:slug defaults)
                                "created"     (-> defaults
                                                  :created_at
                                                  :date
                                                  moment
                                                  .fromNow)
                                "total likes" (:likes defaults)}]]]


          [:hr]]

         [:div.col-9

          [form-item/view
           "Title"
           [:div.row
            [:div.col-6
             [:p.small.hint-text "what should we call this awesomeness?"]
             [:input.form-control
              {:value       title
               :placeholder "Project title"
               :on-blur     #(title-blur-handler % slug)
               :on-change   #(rf/dispatch [:update-form-field
                                           ;; which form?
                                           :update-project
                                           ;; which field?
                                           :title
                                           ;; new value
                                           (-> % .-target .-value)])}]]

            [:div.col-6
             [:p.small.hint-text (suggested-slug title)]
             [:input.form-control
              {:value       slug
               :placeholder "Project slug"
               :on-change   #(rf/dispatch [:update-form-field
                                           ;; which form?
                                           :update-project
                                           ;; which field?
                                           :slug
                                           ;; new value
                                           (-> % .-target .-value)])}]]]]

          [form-item/view
           "Description"
           [:input.form-control
            {:value     description
             :on-change #(rf/dispatch [:update-form-field
                                       :update-project
                                       :description
                                       (-> % .-target .-value)])}]]

          [form-item/view
           {:title         "Images"
            :collapsable?  true
            :is-collapsed? (and (not (nil? (:display images)))
                                (not (nil? (:thumbnail images))))}
           [:div.row
            [:div.col-6
             [:div.text-center.text-uppercase.hint-text.small.m-b-20 "Thumbnail"]
             [project-image/render
              {:current-image (:thumbnail images)
               :width         500
               :on-delete     (on-project-image-delete "thumbnail")
               :on-update     (on-project-image-update "thumbnail")}]]

            [:div.col-6
             [:div.text-center.text-uppercase.hint-text.small.m-b-20 "Display"]
             [project-image/render
              {:current-image (:display images)
               :width         500
               :on-delete     (on-project-image-delete "display")
               :on-update     (on-project-image-update "display")}]]]]

          [form-item/view
           "Media nodes"
           [media-node-buttons/view
            {:on-button-click (fn [node-type]
                                (rf/dispatch [:create-media-node
                                              node-type
                                              (:id defaults)]))}]
           (when (count media-nodes)
             [media-nodes-list/render media-nodes])]

          [form-item/view
           "Publishing options"
           (if is-published?
             [:<>
              [:div.pretty.p-icon.p-round
               [:input {:type      :checkbox
                        :checked   is-published?
                        :on-change #(rf/dispatch [:update-form-field
                                                  :update-project
                                                  :published
                                                  (not is-published?)])}]

               [:div.state.p-success
                [:i.icon.fa.fa-check]
                [:label "This project was published " (unix-from-now published-at)]]]]

             [:<>
              [:p.small.hint-text
               "This project is not published yet. "
               (when (and (not (nil? published-at))
                          (< (to-timestamp (js/Date.)) published-at))
                 (str "It will be published " (unix-from-now published-at)))]

              [:hr]

              [:div.row>div.col-md-4
               [:> dp/DayPicker {:on-day-click  #(rf/dispatch [:update-form-field
                                                               :update-project
                                                               :published_at
                                                               (to-timestamp %)])
                                 :selected-days (js/Date. (* published-at 1000))}]]])]


          [form-item/view
           "Gallery options"
           [:div.row>div.col-12
            (into
             [:div.form-group]
             (for [gallery @galleries]
               ^{:key (:id gallery)}
               [:div.pretty.p-default.p-round
                [:input {:type      :radio
                         :checked   (= (:id gallery) gallery-id)
                         :on-change (fn [e]
                                      (rf/dispatch [:update-form-field
                                                    :update-project
                                                    :gallery_id
                                                    (:id gallery)]))
                         :name      :gallery}]

                [:div.state.p-danger
                 [:label (:name gallery)]]]))]]

          [form-item/view
           "Tags"
           [:> tagsinput {:value tags
                          :on-change #(rf/dispatch [:update-form-field
                                                    :update-project
                                                    :tags
                                                    %])}]]

          [form-item/view
           "Should this project be featured?"
           [:div.row>div.col-2>div.pretty.p-icon.p-round
            [:input {:type    :checkbox
                     :checked is-featured?
                     :on-change
                     (fn []
                       (let [update-field #(rf/dispatch [:update-form-field
                                                         :update-project
                                                         :featured
                                                         (not is-featured?)])
                             remove-image #(rf/dispatch [:delete-project-image
                                                         {:project-id (:id defaults)
                                                          :image-type "featured"}])]
                         ;; if project is featured AND there is an image set
                         (if (and is-featured? (:featured images))
                           ;; confirm whether we want to leave image as is or to remove it as well
                           (rf/dispatch [:confirm {:title      "Umm..."
                                                   :message    "Should I also remove the feature image?"
                                                   :on-confirm #(do (remove-image) (update-field))
                                                   :on-cancel  #(update-field)}])

                           ;; otherwise, just update the field
                           (update-field))))}]

            [:div.state.p-success
             [:i.icon.fa.fa-check]
             [:label (if is-featured? "Yes" "No")]]]

           [:br]

           (when is-featured?
             [:div.row>div.col-md-12
              [project-image/render {:current-image (:featured images)
                                     :width         300
                                     :on-delete     (on-project-image-delete "featured")
                                     :on-update     (on-project-image-update "featured")}]])]

          [form-item/view
           {:title             "All done and ready to go?"
            :show-in-progress? false
            :sub-title         (str "Last updated " (-> defaults
                                                        :updated_at
                                                        :date
                                                        moment
                                                        .fromNow))}
           [:div.row>div.col-md-9
            [:button.btn.btn-info
             {:disabled (or is-updating? (not is-valid?))

              :on-click (fn [e]
                          (.preventDefault e)
                          (rf/dispatch [:update-project (:values @form-state)]))}
             "Save"]

            " or "

            [:button.btn.btn-danger
             {:on-click
              (fn [e]
                (.preventDefault e)
                (rf/dispatch [:confirm {:title      "Behold!"
                                        :message    (str  "Are you sure you want to delete " title "?")
                                        :on-confirm #(rf/dispatch [:delete-project (:id defaults)])}]))}
             "Delete"]]]]

         (when (pos? (count form-items))
           [:div.col-3.form-progress-container
            [form-progress/render form-items]])]))))

(defn render
  []
  (let [is-loading? (rf/subscribe [:is-loading? :project])
        form-items  (rf/subscribe [:project-form-items])
        project     (rf/subscribe [:active-project])]
    (fn []
      (if @is-loading? [loading/render] [main-panel @project @form-items]))))
