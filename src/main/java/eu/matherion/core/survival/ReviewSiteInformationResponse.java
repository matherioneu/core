package eu.matherion.core.survival;

public record ReviewSiteInformationResponse(int review_count, String rating, String last_review_name, int last_review_rate,
                                            int votes, String last_vote) {

}
