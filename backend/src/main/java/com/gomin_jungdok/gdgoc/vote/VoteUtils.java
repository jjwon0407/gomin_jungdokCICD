package com.gomin_jungdok.gdgoc.vote;

import com.gomin_jungdok.gdgoc.vote_option.VoteOption;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class VoteUtils {

    public static Map<String, Object> calculateVoteResults(List<VoteOption> voteOptions, VoteRepository voteRepository, boolean isVisible) {
        //TODO optionContent는 항상 반환되어야 하는데, 이 로직을 별도로 분리하는 효율적인 방법 없을지 고민
        String option1Content = null;
        String option2Content = null;
        Long option1Votes = null;
        Long option2Votes = null;
        String option1Percentage = null;
        String option2Percentage = null;

        for (VoteOption option : voteOptions) {
            if (option.getOrder() == 1) {
                option1Content = option.getText();
                if (isVisible) {
                    option1Votes = voteRepository.countByVoteOptionId(option.getId());
                }
            } else if (option.getOrder() == 2) {
                option2Content = option.getText();
                if (isVisible) {
                    option2Votes = voteRepository.countByVoteOptionId(option.getId());
                }
            }
        }

        //TODO 100%를 맞추기 위해 100에서 옵션1 비율을 빼서 옵션2 비율을 계산중 더 정확하고 효율적인 방법 생각해보기
        if (option1Votes != null && option2Votes != null) { //isVisible로 조건문 수정해도 작동하나, 이 경우 NullPointException 경고 메세지 나타남
            Long totalVotes = option1Votes + option2Votes;
            option1Percentage = (totalVotes > 0) ? (option1Votes * 100 / totalVotes) + "%" : "0%";
            option2Percentage = (totalVotes > 0) ? 100 - (option1Votes * 100 / totalVotes) + "%" : "0%";
        }

        Map<String, Object> result = new HashMap<>();
        result.put("option1Content", option1Content);
        result.put("option2Content", option2Content);
        result.put("option1Votes", option1Votes);
        result.put("option2Votes", option2Votes);
        result.put("option1Percentage", option1Percentage);
        result.put("option2Percentage", option2Percentage);

        return result;
    }
}