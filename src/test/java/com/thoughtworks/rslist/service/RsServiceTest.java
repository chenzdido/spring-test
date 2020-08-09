package com.thoughtworks.rslist.service;

import com.thoughtworks.rslist.domain.Trade;
import com.thoughtworks.rslist.domain.Vote;
import com.thoughtworks.rslist.dto.RsEventDto;
import com.thoughtworks.rslist.dto.TradeDto;
import com.thoughtworks.rslist.dto.UserDto;
import com.thoughtworks.rslist.dto.VoteDto;
import com.thoughtworks.rslist.repository.RsEventRepository;
import com.thoughtworks.rslist.repository.TradeRepository;
import com.thoughtworks.rslist.repository.UserRepository;
import com.thoughtworks.rslist.repository.VoteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

class RsServiceTest {
  RsService rsService;

  @Mock RsEventRepository rsEventRepository;
  @Mock UserRepository userRepository;
  @Mock VoteRepository voteRepository;
  @Mock TradeRepository tradeRepository;
  LocalDateTime localDateTime;
  Vote vote;

  @BeforeEach
  void setUp() {
    initMocks(this);
    rsService = new RsService(rsEventRepository, userRepository, voteRepository,tradeRepository);
    localDateTime = LocalDateTime.now();
    vote = Vote.builder().voteNum(2).rsEventId(1).time(localDateTime).userId(1).build();
  }

  @Test
  void shouldVoteSuccess() {
    // given

    UserDto userDto =
        UserDto.builder()
            .voteNum(5)
            .phone("18888888888")
            .gender("female")
            .email("a@b.com")
            .age(19)
            .userName("xiaoli")
            .id(2)
            .build();
    RsEventDto rsEventDto =
        RsEventDto.builder()
            .eventName("event name")
            .id(1)
            .keyword("keyword")
            .voteNum(2)
            .user(userDto)
            .build();

    when(rsEventRepository.findById(anyInt())).thenReturn(Optional.of(rsEventDto));
    when(userRepository.findById(anyInt())).thenReturn(Optional.of(userDto));
    // when
    rsService.vote(vote, 1);
    // then
    verify(voteRepository)
        .save(
            VoteDto.builder()
                .num(2)
                .localDateTime(localDateTime)
                .user(userDto)
                .rsEvent(rsEventDto)
                .build());
    verify(userRepository).save(userDto);
    verify(rsEventRepository).save(rsEventDto);
  }


  @Test
  public void shouldBuySuccess(){
    //given
    Trade trade=Trade.builder()
            .amount(150)
            .keyword("经济")
            .eventName("猪肉涨价了")
            .delete("No")
            .build();
    UserDto userDto =
            UserDto.builder()
                    .voteNum(5)
                    .phone("18888888888")
                    .gender("female")
                    .email("a@b.com")
                    .age(19)
                    .userName("chenz")
                    .build();
    RsEventDto rsEventDto=RsEventDto.builder()
            .user(userDto)
            .eventName("eventName")
            .keyword("KeyWord")
            .voteNum(1)
            .rank(4)
            .price(100)
            .build();
    when(rsEventRepository.findByRank(anyInt())).thenReturn(Optional.of(rsEventDto));
    //when
    rsService.buy(trade,2);
    //then
    verify(tradeRepository).save(TradeDto.builder()
            .amount(150)
            .rank(4)
            .rsEvent(rsEventDto)
            .build());
    verify(rsEventRepository).save(rsEventDto);
    assertEquals(rsEventDto.getPrice(),150);
  }

  @Test
  public void shouldBuyThrowVoteNotValidException(){
    //given
    Trade trade=Trade.builder()
            .amount(50)
            .keyword("经济")
            .eventName("猪肉涨价了")
            .delete("No")
            .build();
    UserDto userDto =
            UserDto.builder()
                    .voteNum(5)
                    .phone("18888888888")
                    .gender("female")
                    .email("a@b.com")
                    .age(19)
                    .userName("chenz")
                    .build();
    RsEventDto rsEventDto=RsEventDto.builder()
            .user(userDto)
            .eventName("eventName")
            .keyword("KeyWord")
            .voteNum(1)
            .rank(4)
            .price(100)
            .build();
    when(rsEventRepository.findByRank(anyInt())).thenReturn(Optional.of(rsEventDto));
    //when&then
    assertThrows(RuntimeException.class,()->
            rsService.buy(trade,4));
  }

  @Test
  void shouldThrowExceptionWhenUserNotExist() {
    // given
    when(rsEventRepository.findById(anyInt())).thenReturn(Optional.empty());
    when(userRepository.findById(anyInt())).thenReturn(Optional.empty());
    //when&then
    assertThrows(
        RuntimeException.class,
        () -> {
          rsService.vote(vote, 1);
        });
  }
}
