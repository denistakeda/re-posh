# data-frame

DataFrame is a ClojureScript library that allows you to use [re-frame](https://github.com/Day8/re-frame) with [DataScript](https://github.com/tonsky/datascript) as a data storage. It uses [posh](https://github.com/mpdairy/posh) under the hood.
I like the refined beauty of re-frame but it forces me to optimaze my store and give it certan shape. At the same time I like freedom and power of DataScript and Datalog. So why not to use them together. 
I want to write subscriptions on Datalog and events as a transaction. If you want the same, welcome.

## Usage

Start a project and include this dependency:
```
[data-frame "0.1.1"]
```
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

There are two ways to subscribe to DataScript database. Throw query and throw pull

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
Every parameter in signal will be pass as param to the query
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
 
 I used totally the same solution as re-frame `reg-event-db` but with datascript database instead. Function `reg-event-ds` takes event name and event handler. First param for handler is a dereferenced DataScript database. You can do with it whatewer you like, make query or take entities with pull. The second parameter is a signal. Event handler have to return transaction.
 
 ```clojure
 (reg-event-ds
  :update-task
  (fn [ds [_ id path value]] ;; ds is not used here, just an example
    [[:db/add id path value]]))
 ```
 
 ## License
 
 Copyright © 2015 Denis Krivosheev
 
 Distributed under the MIT License

