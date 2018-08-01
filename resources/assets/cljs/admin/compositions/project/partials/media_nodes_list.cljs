(ns admin.compositions.project.partials.media-nodes-list
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [cljss.core :refer-macros [defstyles]]
            [admin.components.collection.view :as collection-view]
            [admin.compositions.project.partials.text-node :as text-node]
            [admin.compositions.project.partials.image-node :as image-node]
            [admin.compositions.project.partials.video-node :as video-node]
            ))

(defn- resolve-node-component
  [type]
  (case type
    "text"  text-node/render
    "video" video-node/render
    "image" image-node/render))

(defn- toggle-collapse-handler
  [is-collapsed?]
  (fn [e]
    (.preventDefault e)
    (reset! is-collapsed? (not @is-collapsed?))))

(defn- on-delete-handler
  [id]
  (fn [e]
    (.preventDefault e)
    (rf/dispatch [:confirm {:title      "Are you sure?"
                            :message    "This node will be deleted permanently"
                            :on-confirm #(rf/dispatch [:delete-project-node id])}])))

(defn- node-item-controls
  [{:keys [on-toggle on-delete is-collapsed?]}]
  [:div.pull-right
   [:ul.list-inline.no-margin
    [:li
     [:a {:href     "#"
          :on-click on-toggle}
      [:i.pg
       {:class (if is-collapsed? "pg-arrow_down" "pg-arrow_up")}]]]
    [:li
     [:a {:href     "#"
          :on-click on-delete}
      [:i.pg.pg-trash]]]]])

(defn- node-item-renderer
  [_ {node-type     :type
      is-collapsed? :is-collapsed?
      id            :id
      :or           {is-collapsed? true}}]
  (let [component     (resolve-node-component node-type)
        is-collapsed? (r/atom is-collapsed?)]
    (fn [draggable-props {node-type :type
                         node-item :node_item}]
      [:div.card.social-card.share.no-border.p-b-0.m-b-10.share-other.full-height.full-width.sortable-node-item
       draggable-props
       [component
        {:node-id       id
         :node-item     node-item
         :on-update     (fn [node-item]
                          (rf/dispatch [:update-media-node
                                        id
                                        node-item]))
         :is-collapsed? @is-collapsed?
         :controls      [node-item-controls
                         {:on-toggle     (toggle-collapse-handler is-collapsed?)
                          :on-delete     (on-delete-handler id)
                          :is-collapsed? @is-collapsed?}]}]])))

(defn render
  [nodes-collection]
  [collection-view/render
   (merge
    nodes-collection
    {:item-renderer         node-item-renderer
     :collection-order-path [:active-project :mediaNodes :order]})])
