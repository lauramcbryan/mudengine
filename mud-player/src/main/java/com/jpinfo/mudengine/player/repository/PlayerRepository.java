package com.jpinfo.mudengine.player.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.jpinfo.mudengine.player.model.MudPlayer;

public interface PlayerRepository extends CrudRepository<MudPlayer, Long> {

	Optional<MudPlayer> findByUsername(String username);
	
	Optional<MudPlayer> findByUsernameAndPassword(String username, String password);
}
