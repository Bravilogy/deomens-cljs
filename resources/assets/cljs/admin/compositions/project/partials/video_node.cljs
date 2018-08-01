(ns admin.compositions.project.partials.video-node
  (:require ["moment" :as moment]
            [reagent.core :as r]
            [re-frame.core :as rf]
            [clojure.string :as s]
            [cljss.core :refer-macros [defstyles]]
            [admin.components.project-image.view :as project-image]
            ))

(def video-providers {:youtube {:color "red"
                                :icon  "fa-youtube"
                                :label "YouTube"}
                      :vimeo   {:color "#1ab7ea"
                                :icon  "fa-vimeo"
                                :label "Vimeo"}
                      :other   {:color "red"
                                :icon  "fa-play-circle"
                                :label "external source"}})

(defstyles node-icon
  [color]
  {:color color})

(defn- resolve-video-provider
  [url]
  (condp #(s/includes? %2 %1) url
    "youtube" :youtube
    "vimeo"   :vimeo
    :other))

(defn- format-title
  [caption vendor has-thumbnail?]
  (str
   (if (not-empty caption)
     (str caption " ")
     "Video")
   (str " on " vendor)
   (when has-thumbnail? " (with thumbnail)")))

(defn render
  [{on-update :on-update
    node-id   :node-id
    node-item :node-item}]
  (rf/dispatch [:reset-form :edit-node (:id node-item) node-item])
  (let [form-state (rf/subscribe [:form-state :edit-node (:id node-item)])]
    (fn [{is-collapsed?          :is-collapsed?
         controls               :controls
         {caption    :caption
          url        :url
          thumbnail  :thumbnail
          created-at :created_at
          :as        node-item} :node-item}]
      (let [{v-color :color
             v-icon  :icon
             v-label :label} (get video-providers
                                  ;; resolve the provider key
                                  (if url
                                    (resolve-video-provider url)
                                    :other))]

        [:<>
         [:div.card-header
          controls
          [:div.user-pic
           [:i.fa {:class [(node-icon v-color)
                           v-icon]}]]

          [:h5 (format-title caption v-label (not (nil? thumbnail)))]
          (when url
            [:h6
             [:a {:href   url
                  :target :_blank} url]])]

         (when-not is-collapsed?
           [:<>
            [:div.card-description
             [:div.row
              [:div.col-6
               [:div.form-group
                [:label.required "Url"]
                [:input.form-control
                 {:type      :text
                  :value     (:url (:values @form-state))
                  :on-blur   #(on-update (:values @form-state))
                  :on-change #(rf/dispatch [:update-form-field
                                            [:edit-node (:id node-item)]
                                            :url
                                            (-> % .-target .-value)])}]]]

              [:div.col-6
               [:div.form-group
                [:label "Caption"]
                [:input.form-control
                 {:type      :text
                  :value     (:caption (:values @form-state))
                  :on-blur   #(on-update (:values @form-state))
                  :on-change #(rf/dispatch [:update-form-field
                                            [:edit-node (:id node-item)]
                                            :caption
                                            (-> % .-target .-value)])}]]]]

             [:div.row>div.col-12
              [:div.form-group
               [:label "Thumbnail"]
               [project-image/render
                {:current-image thumbnail
                 :on-update     (fn [[image]]
                                  (rf/dispatch-sync [:update-form-field
                                                     [:edit-node (:id node-item)]
                                                     :thumbnail
                                                     image])
                                  (on-update (:values @form-state)))
                 :on-delete     #(rf/dispatch [:remove-node-image node-id])}]]]]
            [:div.card-footer.clearfix.m-t-10
             [:div.time
              "Created " (-> created-at moment .fromNow)]]])]))))
