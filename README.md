[![Clojars Project](https://img.shields.io/clojars/v/data-frame.svg)](https://clojars.org/data-frame)

# data-frame

DataFrame is a ClojureScript library that allows you to use [re-frame](https://github.com/Day8/re-frame) with [DataScript](https://github.com/tonsky/datascript) as a data storage. It uses [posh](https://github.com/mpdairy/posh) under the hood.

DataFrame is not replaced re-frame, but complements it. You can use DataScript database only or use it together with the re-frame database.

I like the refined beauty of re-frame but it forces me to optimize my store and give it the certain shape. At the same time, I like freedom and power of DataScript and Datalog. So why not to use them together. I want to write subscriptions on Datalog and events as a transaction. If you want the same, welcome.

## Usage

Require it in your app:
```clojure
(ns example
  (:require [reagent.core :as r]
            [data-frame.core :refer [connect! reg-query-sub reg-pull-sub reg-event-ds]]
            [datascript.core :as d]))
```

## Connection

First you have to connect your DataScript database to data-frame.

```clojure
(ns example.db
  (:require
    [datascript.core    :as d]
    [data-frame.core    :refer [connect!]]))

(def conn (d/create-conn))
(connect! conn)
```

## Subscriptions

There are two ways to subscribe to DataScript database. With query and with pull subscriptions.

### Query subscription

You can use `reg-query-sub` function for subscribe to any query

```clojure
(reg-query-sub
  :task-ids
  '[ :find  [?tid ...]
     :where [?tid :task/title]])
```

This function takes two params, subscription name and datalog [query](http://docs.datomic.com/query.html#sec-5). You can use this subscription as regular re-frame subscription

```clojure
(defn page []
  (let [task-ids (subscribe [:task-ids])]
    (fn []
       ...
```

Every parameter in a signal will be pass as param to the query

```clojure
(reg-query-sub
   :task-ids
   '[ :find  [?tid ...]
      :in $ ?param-1 ?param-2
      :where ...

(let [task-ids (subscribe [:task-ids param-1 param-2])]
   ...)
```

### Pull subscription

Pull subscriptions creates subscription to the entity. `reg-pull-sub` function create pull subscription and takes two params, subscription name and pull pattern. For more details see [Datomic Pull](http://docs.datomic.com/pull.html)

```clojure
(reg-pull-sub
   :sub-name
   '[*])

 ;; Usage

 (let [entity-id 123
       entity    (subscribe [:sub-name entity-id])])
 ```

 ## Events

DataFrame uses totally the same solution as re-frame `reg-event-db` but with datascript database instead. Function `reg-event-ds` takes event name and event handler. First param for handler is a dereferenced DataScript database. You can do with it whatewer you like, make query or take entities with pull. The second parameter is a signal. Event handler have to return transaction.

 ```clojure
 (reg-event-ds
  :update-task
  (fn [ds [_ id path value]] ;; ds is not used here, just an example
    [[:db/add id path value]]))
 ```

 ## Effects and Co-effects

DataFrame introduce one effect and one co-effect. You can use them as regular re-frame effects and co-effects (in fact they are regular re-frame effects and coeffects)


### Transact effect

This effect commit transaction into the DataScript database

```clojure
(ns example.events
   (:require [re-frame.core :as r]))

(r/reg-event-fx
   :my-event
   (fn [cofx [_ id k v]]
      {:transact [[:db/add id k v]]})) ;; return datascript transaction
```

### DS co-effect

This co-effect provide DataScript database into your event handler

```clojure
(ns example.events
   (:require [re-frame.core :as r]))

(r/reg-event-fx
   :my-event
   [(r/inject-cofx :ds)] ;; inject coeffect
   (fn [{:keys [ds]} [_ id k v]] ;; ds here is the DataScript database
      {:transact [[:db/add id k v]]}))
```

## Contribution

Pull requests are welcome. Email me on <denis.takeda@gmail.com> if you have any questions, suggestions or proposals.

 ## License

 Copyright © 2015 Denis Krivosheev

 Distributed under the MIT License

