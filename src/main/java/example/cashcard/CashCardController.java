package example.cashcard;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriBuilderFactory;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Date;
import java.util.Optional;

/**
 * Author: Khandaker Maruf
 * Date: 05 Apr 2024
 */
@RestController
@RequestMapping("/cashcards")
public class CashCardController {

    private final CashCardRepository cashCardRepository;

    public CashCardController(CashCardRepository cashCardRepository) {
        this.cashCardRepository = cashCardRepository;
    }

    @GetMapping("/{requestedId}")
    public ResponseEntity<CashCard> findById(@PathVariable Long requestedId) {
        Optional<CashCard> cashCardOptional = cashCardRepository.findById(requestedId);

        return cashCardOptional.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build())         ;

    }

    @PostMapping
    public ResponseEntity<Void> createCashCard(@RequestBody CashCard newCashCardRequest,
                                               UriComponentsBuilder uriBuilder) {
        CashCard createdCashCard = cashCardRepository.save(newCashCardRequest);

        System.out.println(new Date());

//        URI uri = uriBuilder.

        return ResponseEntity.created(URI.create("/cashcards/" + createdCashCard.id())).build();
    }
}