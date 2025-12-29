package com.uh2.devops.cheque;

import com.uh2.devops.cheque.Cheque.ChequeStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/cheques") // Préfixe demandé dans tes specs
public class ChequeController {

    private final ChequeRepository repository;

    public ChequeController(ChequeRepository repository) {
        this.repository = repository;
    }

    // 1. Register/issue a cheque
    @PostMapping
    public Cheque registerCheque(@RequestBody Cheque cheque) {
        cheque.setStatus(ChequeStatus.ISSUED); // Statut initial forcé
        return repository.save(cheque);
    }

    // 2. Get cheque by id (A FAIRE PAR SAAD )
    @GetMapping("/{id}")
    public Cheque getChequeById(@PathVariable Long id) {
        // TODO: Implémenter la gestion d'erreur 404
        return repository.findById(id).orElse(null);
    }

    // 3. Search / list cheques (A FAIRE PAR TAHA )
    @GetMapping
    public List<Cheque> searchCheques(
        @RequestParam(required = false) String accountId,
        @RequestParam(required = false) String chequeNumber) {

     if (accountId != null && chequeNumber != null) {
        return repository.findByAccountIdAndChequeNumber(accountId, chequeNumber);
        }

        if (accountId != null) {
        return repository.findByAccountId(accountId);
     }

        if (chequeNumber != null) {
        return repository.findByChequeNumber(chequeNumber);
     }

        // Aucun filtre → retourner tous les chèques
         return repository.findAll();
    }

    // 4. Deposit a cheque (A FAIRE PAR Anouar)
    @PostMapping("/{id}/deposit")
    public Cheque depositCheque(@PathVariable Long id, @RequestBody Cheque payload) {
        return repository.findById(id).map(cheque -> {
            cheque.setStatus(ChequeStatus.DEPOSITED);
            cheque.setDepositDate(payload.getDepositDate());
            cheque.setDepositAccountId(payload.getDepositAccountId());
            return repository.save(cheque);
        }).orElseThrow(() -> new RuntimeException("Cheque not found"));
    }

    // 5. Clear a cheque (Validation / Encaissement)
    @PostMapping("/{id}/clear")
    public Cheque clearCheque(@PathVariable Long id, @RequestBody Cheque payload) {
        return repository.findById(id).map(cheque -> {
            // On passe le statut à CLEARED et on enregistre la date
            cheque.setStatus(ChequeStatus.CLEARED);
            cheque.setClearingDate(payload.getClearingDate());
            return repository.save(cheque);
        }).orElseThrow(() -> new RuntimeException("Cheque not found with id: " + id));
    }

    // 6. Reject a cheque (Rejet pour solde insuffisant, signature, etc.)
    @PostMapping("/{id}/reject")
    public Cheque rejectCheque(@PathVariable Long id, @RequestBody Cheque payload) {
        return repository.findById(id).map(cheque -> {
            cheque.setStatus(ChequeStatus.REJECTED);
            cheque.setRejectionReason(payload.getRejectionReason());
            return repository.save(cheque);
        }).orElseThrow(() -> new RuntimeException("Cheque not found with id: " + id));
    }

    // 7. Cancel a cheque (Annulation par l'émetteur)
    @PostMapping("/{id}/cancel")
    public Cheque cancelCheque(@PathVariable Long id) {
        return repository.findById(id).map(cheque -> {
            // Règle métier : On ne peut annuler que si le chèque est encore au début (ISSUED)
            // Si le chèque est déjà encaissé ou rejeté, c'est trop tard.
            if (cheque.getStatus() == ChequeStatus.ISSUED) {
                cheque.setStatus(ChequeStatus.CANCELLED);
                return repository.save(cheque);
            } else {
                throw new RuntimeException("Cannot cancel a cheque that has already been processed (current status: " + cheque.getStatus() + ")");
            }
        }).orElseThrow(() -> new RuntimeException("Cheque not found with id: " + id));
    }
}