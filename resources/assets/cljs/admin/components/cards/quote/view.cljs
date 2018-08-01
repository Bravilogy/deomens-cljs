(ns admin.components.cards.quote.view
  (:require [re-frame.core :as re-frame]))

(def styles {:blockquote {:padding-left 0
                          :margin-bottom 0
                          :margin-top 15}})

(defn render
  [quote]
  [:div.card.social-card.share.full-width.m-b-10.no-border {:data-social :item}
   [:div.card-header
    [:h5.text-complete.pull-left.fs-12 "Random quote"]]

   [:div.card-description
    [:blockquote {:style (:blockquote styles)}
     [:p {:dangerouslySetInnerHTML {:__html (:content quote)}}]
     [:small (:author quote)]]]

   [:div.card-footer.flearfix {:on-click #(re-frame/dispatch [:get-random-quote])}
    [:div.pull-left
     [:span.text-complete "Refresh"]]
    [:div.pull-right.hint-text]
    [:div.clearfix]]])

