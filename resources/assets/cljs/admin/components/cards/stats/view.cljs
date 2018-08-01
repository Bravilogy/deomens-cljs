(ns admin.components.cards.stats.view)

(defn- total-subscriptions
  [{recent-subscription :recent_subscription}]
  [:span "Last subscription was " [:strong recent-subscription]])

(defn- total-likes
  [{ liked-at :liked_at url :url title :title }]
  [:<>
   "Latest like was "
   [:strong liked-at]
   " for "
   [:br]
   [:a.text-complete {:href   url
                      :target :_blank}
    title]])

(defn- get-bar-color
  [type]
  (case (keyword type)
    :totalLikes         "progress-bar-danger"
    :totalSubscriptions "progress-bar-complete"))

(defn- stat-renderer
  [{label :label
    value :value
    type  :type
    extra :extra}]

  (let [extras-renderer (case (keyword type)
                          :totalLikes         total-likes
                          :totalSubscriptions total-subscriptions)]
    [:<>
     [:div.row-xs-height>div.col-xs-height.col-top>div.card-header.top-left.top-right>div.card-title
      [:span.font-montserrat.fs-11.all-caps label]]

     [:div.row-xs-height>div.col-xs-height.col-top>div.p-l-20.p-t-50.m-b-20
      [:h3.no-margin.p-b-10 value]
      [:span.small.hint-text (extras-renderer extra)]]

     [:div.row-xs-height>div.col-xs-height.col-bottom>div.progress.progress-small.m-b-0
      [:div {:class ["progress-bar" (get-bar-color type)]
             :style {:width "100%"}}]]]))

(defn- stat-item
  [{:keys [type] :as item}]
  ^{:key type}
  [:div.card.no-border.widget-loader-bar.m-b-10
   [:div.container-xs-height.full-height
    (stat-renderer item)]])

(defn render
  [stats]
  [:div (map stat-item (filter :value stats))])
