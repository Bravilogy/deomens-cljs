(ns admin.compositions.projects.partials.top-from-active-gallery
  (:require [re-frame.core :as rf]
            [admin.routes :as routes]))

(defn render
  [top-projects]
  [:div.card.card-transparent
   [:div.card-header
    [:div.card-title "Top " (count top-projects) " from gallery"]]
   [:ul.list-unstyled
    (for [{:keys [id images title]} top-projects]
      ^{:key id}
      [:li
       [:div.thumbnail-wrapper.d32.circular.b-white.m-r-5.b-a.b-white
        [:a {:href  (routes/project {:id id})
             :title title}
         [:img {:src    (:display images)
                :alt    title
                :height 35
                :width  35}]]]])]])
