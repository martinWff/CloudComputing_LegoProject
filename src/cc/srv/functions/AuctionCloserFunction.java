package cc.srv.functions;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.*;

public class AuctionCloserFunction {
    
    @FunctionName("CloseExpiredAuctions")
    public void run(
        @TimerTrigger(
            name = "timerInfo",
            schedule = "0 */5 * * * *"  // every 5 minutes
        ) String timerInfo,
        final ExecutionContext context) {
        
        context.getLogger().info("🔍 Checking expired auctions");
        
        // SIMULATION 
        List<Auction> expiredAuctions = findExpiredAuctions();
        
        for (Auction auction : expiredAuctions) {
            context.getLogger().info("✅ Close Auction: " + auction.id 
              );
            
            // Use DB update logic here
        }
        
        context.getLogger().info("🎯 " + expiredAuctions.size() 
            + " auctions closed.");
    }
    
    // Inner class to represent an auction

    public static class Auction {
        public String id;
        public String legoSetName;
        public double currentBid;
        public String winner;
        public LocalDateTime endDate;
        
        public Auction(String id, String name, double bid, String winner, LocalDateTime end) {
            this.id = id;
            this.legoSetName = name;
            this.currentBid = bid;
            this.winner = winner;
            this.endDate = end;
        }
    }
    
    // Méthode de simulation
    private List<Auction> findExpiredAuctions() {
        List<Auction> auctions = new ArrayList<>();
        
        // Données de test
        auctions.add(new Auction("AUC-001", "Lego Millennium Falcon", 
            150.0, "user123", LocalDateTime.now().minusHours(1)));
            
        auctions.add(new Auction("AUC-002", "Lego Eiffel Tower", 
            89.0, "user456", LocalDateTime.now().minusMinutes(30)));
         return auctions;
        
        }
        
        
        
        }
            
   