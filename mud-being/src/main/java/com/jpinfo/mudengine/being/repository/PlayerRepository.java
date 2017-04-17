package com.jpinfo.mudengine.being.repository;

import org.springframework.data.repository.CrudRepository;

import com.jpinfo.mudengine.being.model.MudPlayer;

public interface PlayerRepository extends CrudRepository<MudPlayer, Integer> {

}
