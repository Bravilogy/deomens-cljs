(ns admin.compositions.project.subs
  (:require [re-frame.core :as rf]))

(defn active-project [db]
  (:active-project db))

(defn project-form-items [db]
  (:project-form-items db))

#_(+ 10 (- @scroll-top (.-offsetHeight @progress)))

#_(defn floating-top
    [db [_ {:keys [container floating-element start-offset]}]]
    (when (and @container @floating-element)
      (let [container-top (-> @container
                              .getBoundingClientRect
                              .-top)
            scroll-to     (+ start-offset
                             ())
            ])
      )
    100)

(defn- hit-scroll-spot?
  "has the scroll top hit the scroll point
  when the list starts to float?"
  ([top] (hit-scroll-spot? top 70))
  ([top break-point] (< top break-point)))

(rf/reg-sub
 :floating-top
 (fn [_]
   (rf/subscribe [:scroll-top]))
 (fn [scroll-top [_ {:keys [container floating-element]}]]
   (if (and @container @floating-element)
     (let [container-top       (-> @container
                                   .getBoundingClientRect
                                   .-top)
           container-top-const (+ scroll-top container-top)
           scroll-to           (+ 70 (- scroll-top container-top-const))]
       (if (hit-scroll-spot? container-top) scroll-to 0))
     0)))

(rf/reg-sub :active-project active-project)
(rf/reg-sub :project-form-items project-form-items)
