(ns admin.components.cards.popular-project.view
  (:require [admin.routes :as routes]
            [cljss.core :refer-macros [defstyles]]))

(defstyles thumbnail
  [url]
  {:background-size     "cover"
   :background-position "center"
   :background-image    (str "url(" url ")")})

(defn render
  [{:keys [id
           url
           title
           description
           likes
           images]
    :as   original}]
  [:div.card.social-card.share.full-width.m-b-10.no-border.d-flex.full-height
   [:div.card-header.no-border
    [:h5.text-complete.pull-left.fs-12 "Popular project"]]

   [:a.card-content.flex-1 {:href  (routes/project {:id id})
                            :class (thumbnail (:thumbnail images))}]

   [:div.card-description
    [:div title]
    [:small.hint-text description]]

   [:div.card-footer [:i.fa.fa-heart-o] " " likes]])
