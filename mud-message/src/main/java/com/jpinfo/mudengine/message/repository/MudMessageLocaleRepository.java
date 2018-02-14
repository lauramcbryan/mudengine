package com.jpinfo.mudengine.message.repository;

import org.springframework.data.repository.CrudRepository;

import com.jpinfo.mudengine.message.model.MudMessageLocale;
import com.jpinfo.mudengine.message.model.pk.MudMessageLocalePK;

public interface MudMessageLocaleRepository extends CrudRepository<MudMessageLocale, MudMessageLocalePK> {

}
