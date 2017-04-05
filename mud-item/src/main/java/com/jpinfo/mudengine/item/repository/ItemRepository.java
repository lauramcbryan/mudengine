package com.jpinfo.mudengine.item.repository;

import org.springframework.data.repository.CrudRepository;

import com.jpinfo.mudengine.item.model.Item;

public interface ItemRepository extends CrudRepository<Item, Integer> {

}
