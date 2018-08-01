(ns admin.compositions.projects.view
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [clojure.string :as s]
            [cljss.core :refer-macros [defstyles]]
            [admin.components.loading.view :as loading]
            [admin.compositions.projects.partials.overlay-search :as search]
            [admin.compositions.projects.partials.projects-list :as projects-list]
            [admin.compositions.projects.partials.new-project-form :as new-project-form]
            [admin.compositions.projects.partials.top-from-active-gallery :as top-from-active-gallery]
            ))

(defn- projects-info
  "information boxes component on the side"
  [projects-count filtered-count]
  [:<>
   [:div.card.card-default
    [:div.card-header
     "hello"]
    [:div.card-block

     [:h6 "Total number of projects "
      [:span.badge.badge-danger.pull-right
       {:style {:font-size "15px"}}
       projects-count]]]]

   [:div.card.card-default
    (when (not= filtered-count projects-count)
      [:p.small.hint-text
       "Number of projects in gallery: "
       filtered-count])]])

(defn- gallery-tabs
  [galleries active-gallery on-tab-click]
  [:ul.nav.nav-tabs.nav-tabs-linetriangle.gallery-tabs
   (for [{name :name
          :as  gallery} galleries]
     [:li.nav-item {:key name}
      [:a {:href     "#"
           :class    (when (= name active-gallery) :active)
           :on-click (fn [e]
                       (.preventDefault e)
                       (rf/dispatch [:set-active-gallery (:name gallery)]))}
       [:span name]]])])

(defn- main-panel
  []
  (let [active-gallery (rf/subscribe [:active-gallery])
        top-projects   (rf/subscribe [:top-projects-from-active-gallery])]
    (fn
      [projects-count gallery-collections]
      (let [active-gallery      (deref active-gallery)
            active-gallery-name (:name active-gallery)]
        [:div.container.no-padding>div.content-container>div.row
         [:div.col-lg-4
          [new-project-form/render]
          (when (pos? @top-projects)
            [top-from-active-gallery/render @top-projects])
          #_[projects-info projects-count (-> active-collection
                                              :projects
                                              :items
                                              count)]]

         [:div.col-lg-8.p-t-5
          [gallery-tabs
           gallery-collections
           active-gallery-name]
          ;; render all the groups but only
          ;; show an active group based on
          ;; current active gallery name
          (for [{projects :projects
                 name     :name} gallery-collections]
            (when (= active-gallery-name name)
              ^{:key (-> projects :order str)}
              [projects-list/render name projects]))]]))))

(defn render []
  (let [projects            (rf/subscribe [:projects])
        projects-count      (rf/subscribe [:projects-count])
        gallery-collections (rf/subscribe [:gallery-collections])]
    (fn []
      (if (or (nil? @gallery-collections)
              (nil? @projects))
        [loading/render]
        [:<>
         [search/render @projects]
         [main-panel
          @projects-count
          @gallery-collections]]))))
