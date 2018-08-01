(ns admin.compositions.dashboard.view
  (:require [re-frame.core :as re-frame]
            [admin.components.loading.view :as loading]
            [admin.components.cards.stats.view :as stats-card]
            [admin.components.cards.quote.view :as quote-card]
            [admin.components.cards.popular-project.view :as popular-project]))

(defn texty
  []
  [:div.p-b-35.p-l-35.p-r-35
   [:p.fs-16.text-black "Horizontal menu"]
   [:h3.normal.text-black "Executive"]
   [:p.fs-15.p-t-10.p-b-35
    (str "A professional template with a timeless look, best suited "
         "to quickly create a serious organized experience.")]])

(defn main-panel
  [{:keys [statistics quote popular]}]
  [:<>
   [:div.col-lg-3.col-sm-6.d-flex.flex-column
    (when statistics
      [stats-card/render statistics])

    (when quote
      [quote-card/render quote])]

   [:div.col-lg-4.col-sm-6.d-flex.flex-column
    (when popular
      [popular-project/render popular])]

   [:div.col-lg-5.d-flex.flex-column
    [texty]]])

(defn render []
  (let [dashboard-data (re-frame/subscribe [:dashboard-data])
        is-loading?    (re-frame/subscribe [:is-loading? :dashboard])]
    (fn []
      [:div.container.no-padding
       [:div.row
        (if @is-loading?
          [loading/render]
          [main-panel @dashboard-data])]])))
