package example.cashcard;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

/**
 * Author: Khandaker Maruf
 * Date: 05 Apr 2024
 */
@RestController
@RequestMapping("/cashcards")
public class CashCardController {

    @Autowired
    private CashCardRepository cashCardRepository;

//    public CashCardController(CashCardRepository cashCardRepository) {
//        this.cashCardRepository = cashCardRepository;
//    }

    @GetMapping("/{requestedId}")
    public ResponseEntity<CashCard> findById(@PathVariable Long requestedId) {
        Optional<CashCard> cashCardOptional = cashCardRepository.findById(requestedId);

        return cashCardOptional.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build())         ;

    }

    @GetMapping
    public ResponseEntity<List<CashCard>> findAll(Pageable pageable) {
        Page<CashCard> cashCards = cashCardRepository.findAll(
                PageRequest.of(pageable.getPageNumber(),
                        pageable.getPageSize(),
                        pageable.getSortOr(Sort.by(Sort.Direction.ASC, "amount")))
        );

        return ResponseEntity.ok(cashCards.getContent());
    }

    @PostMapping
    public ResponseEntity<Void> createCashCard(@RequestBody CashCard newCashCardRequest) {

        CashCard createdCashCard = cashCardRepository.save(newCashCardRequest);

        URI locationOfCreatedCashCard = UriComponentsBuilder
                .fromPath("/cashcards/{id}")
                .buildAndExpand(createdCashCard.getId())
                .toUri();

        return ResponseEntity.created(locationOfCreatedCashCard).build();
    }
}