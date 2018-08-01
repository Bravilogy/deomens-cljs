(ns admin.components.sortable-list.view
  (:require [reagent.core :as r]
            [cljss.core :refer-macros [defstyles]]))

(defstyles sortable-list__item
  []
  {:user-select "none"})

(defstyles sortable-list__item-dragging
  []
  {:z-index "100"
   :opacity ".25"})

(defstyles card-progress
  []
  {:background-color "transparent"
   :display          :block})

(defstyles sortable-list__item-updating
  []
  {:opacity .4})

(defn- loading
  "a little loading component that will show up
  when a sortable list is being updated"
  []
  [:div.card-progress {:class (card-progress)}
   [:div.progress-circle-indeterminate.progress-circle-master]])

(defn- change-position
  [order pos item]
  (let [items (remove #{item} order)
        head  (take pos items)
        tail  (drop pos items)]
    (concat head [item] tail)))

(defn render
  "this function renders a sortable list.
  it will pass down draggable props to
  the component as a first argument."
  [{:keys [order]}]
  (let [state (r/atom {:order order})]
    (fn [{:keys [items update-order sync-order item-renderer container-class is-updating?]}]
      (let [items (vec items)]
        (into [:div.sortable-list {:class container-class}
               (when is-updating? [loading])]
              (for [[id position] (map vector (:order @state) (range))]
                (when-let [item (first (filter #(= id (:id %)) items))]
                  [item-renderer
                   {:key           id
                    :class         [(sortable-list__item)
                                    (when is-updating?
                                      (sortable-list__item-updating))
                                    (when (= id (:drag-index @state))
                                      (sortable-list__item-dragging))]
                    :draggable     true
                    :on-drag-start #(swap! state assoc :drag-index id)
                    :on-drag-over  (fn [e]
                                     (.preventDefault e)
                                     (swap! state assoc
                                            :drag-over position)
                                     (swap! state update :order
                                            change-position (:drag-over @state) (:drag-index @state)))
                    :on-drag-leave #(swap! state assoc :drag-over :empty)
                    :on-drag-end   (fn []
                                     (swap! state dissoc :drag-index :drag-over)
                                     (when sync-order (sync-order (:order @state))))}
                   item])))))))
