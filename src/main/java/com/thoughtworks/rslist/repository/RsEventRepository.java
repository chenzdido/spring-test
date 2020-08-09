package com.thoughtworks.rslist.repository;

import com.thoughtworks.rslist.dto.RsEventDto;
import com.thoughtworks.rslist.dto.VoteDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface RsEventRepository extends CrudRepository<RsEventDto, Integer> {
  List<RsEventDto> findAll();


  Optional<RsEventDto> findByRank(int rank);

  void deleteByRank(int rank);

  @Override
  void deleteById(Integer integer);

  @Transactional
  void deleteAllByUserId(int userId);
}
