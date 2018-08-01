(ns admin.compositions.projects.partials.projects-list
  (:require ["moment" :as moment]
            [reagent.core :as r]
            [re-frame.core :as rf]
            [com.rpl.specter :as sp]
            [admin.routes :as routes]
            [cljss.core :refer-macros [defstyles]]
            [admin.components.collection.view :as collection-view]
            [admin.components.sortable-list.view :as sortable-list]))

(defstyles project-item-header
  []
  {:border-top "none !important"
   :&:hover    {:background   "transparent !important"
                :border-color "#e6e6e6 !important"}})

(defn- is-in-future?
  [timestamp]
  (let [time-diff (.diff (moment) (.unix moment timestamp))]
    (> 0 time-diff)))

(defn- sortable-project-view
  []
  (let [is-collapsed? (r/atom true)]
    (fn
      [draggable-props
       {id            :id
        title         :title
        description   :description
        images        :images
        published-at  :published_at
        nodes         :nodes
        is-featured?  :featured
        tags          :tags
        is-published? :published
        likes         :likes}]
      [:div.card.social-card.share.no-border.p-b-0.share-other.full-height.full-width.sortable-project-item
       draggable-props

       [:div.card-header
        [:ul.pull-right.list-inline.small.hint-text
         (when is-featured?
           [:li
            [:i.fa.fa-star-o]])

         (when is-published?
           [:li
            [:i.fa.fa-check-square-o]])]

        [:div.user-pic
         [:img {:width  33
                :height 33
                :src    (:thumbnail images)}]]
        [:h5 title]
        (when published-at
          [:h6
           (if (is-in-future? published-at)
             "will be published "
             "was published ")
           (.fromNow (.unix moment published-at))])]

       [:div.card-description
        (when (not-empty description)
          [:p description])

        (when (pos? (count tags))
          [:ul.list-inline.tags-list
           (for [{:keys [name id]} tags]
             ^{:key id}
             [:li.hint-text.small "#" name])])]

       [:div.card-footer.clearfix
        [:ul.time.list-inline
         [:li
          [:a.text-primary
           {:href (routes/project {:id id})}
           [:i.fa.fa-pencil] " edit"]]

         [:li
          [:a.text-primary
           {:href "#"
            :on-click
            (fn [e]
              (.preventDefault e)
              (rf/dispatch [:confirm {:title      "Behold!"
                                      :message    (str  "Are you sure you want to delete " title "?")
                                      :on-confirm #(rf/dispatch [:delete-project id])}]))}
           [:i.fa.fa-trash] " delete"]]]

        [:ul.reactions
         (when (pos? likes)
           [:li
            likes " " [:i.fa.fa-heart-o]])]]])))

(defn render
  [gallery-name collection]
  [collection-view/render
   (merge
    collection
    {:container-class       "card-block row p-t-0"
     :collection-order-path [:gallery-collections
                             sp/ALL
                             #(= gallery-name (:name %))
                             :projects
                             :order]
     :item-renderer         sortable-project-view})])
