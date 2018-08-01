(ns admin.compositions.galleries.subs
  (:require [re-frame.core :as rf]))

(rf/reg-sub
 :galleries
 #(:galleries %))
