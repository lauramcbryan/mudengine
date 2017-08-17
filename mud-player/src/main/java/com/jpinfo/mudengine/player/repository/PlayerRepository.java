package com.jpinfo.mudengine.player.repository;

import org.springframework.data.repository.CrudRepository;

import com.jpinfo.mudengine.player.model.MudPlayer;

public interface PlayerRepository extends CrudRepository<MudPlayer, Long> {

	MudPlayer findByUsername(String username);
}
