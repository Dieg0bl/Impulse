package com.impulse.application;

import java.util.DoubleSummaryStatistics;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.impulse.domain.support.NpsResponse;
import com.impulse.domain.support.CsatResponse;
import com.impulse.application.ports.NpsResponsePort;
import com.impulse.application.ports.CsatResponsePort;

@Service
public class FeedbackService {
    private final NpsResponsePort npsRepo;
    private final CsatResponsePort csatRepo;

    public FeedbackService(NpsResponsePort npsRepo, CsatResponsePort csatRepo){
        this.npsRepo = npsRepo; this.csatRepo = csatRepo;
    }

    @Transactional
    public NpsResponse submitNps(Long userId, int score, String reason){
        if(score <0 || score>10) throw new IllegalArgumentException("score 0-10");
        return npsRepo.save(new NpsResponse(userId, score, reason));
    }

    @Transactional
    public CsatResponse submitCsat(Long userId, int score){
        if(score<1 || score>5) throw new IllegalArgumentException("score 1-5");
        return csatRepo.save(new CsatResponse(userId, score));
    }

    public Map<String,Object> npsAggregate(){
        var all = npsRepo.findAll();
        DoubleSummaryStatistics stats = all.stream().mapToDouble(NpsResponse::getScore).summaryStatistics();
        long promoters = all.stream().filter(r -> r.getScore()>=9).count();
        long detractors = all.stream().filter(r -> r.getScore()<=6).count();
        long total = stats.getCount();
        double nps = total==0?0.0: ((double)(promoters - detractors)/total)*100.0;
        Map<String,Object> out = new HashMap<>();
        out.put("avg", stats.getAverage());
        out.put("nps", nps);
        out.put("total", total);
        return out;
    }

    public Map<String,Object> csatAggregate(){
        var all = csatRepo.findAll();
        DoubleSummaryStatistics stats = all.stream().mapToDouble(CsatResponse::getScore).summaryStatistics();
        Map<String,Object> out = new HashMap<>();
        out.put("avg", stats.getAverage());
        out.put("total", stats.getCount());
        return out;
    }
}
