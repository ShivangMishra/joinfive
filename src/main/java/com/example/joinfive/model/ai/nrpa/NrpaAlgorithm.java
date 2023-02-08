package com.example.joinfive.model.ai.nrpa;

import com.example.joinfive.model.ai.JoinFiveAlgorithm;
import com.example.joinfive.model.grid.Grid;
import com.example.joinfive.model.grid.Line;
import javafx.util.Pair;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

public class NrpaAlgorithm implements JoinFiveAlgorithm {
    private static final double ALPHA = 1;

    @Override
    public Line calcMove(Grid grid) {
        int level = 5;
        NrpaPolicy<Line> policy = new NrpaPolicy<>();
        final long maxRunningTimeMs = 5 * 1000;
        final long endTimeMs = System.currentTimeMillis() + maxRunningTimeMs;
        Pair<Double, List<Line>> result = executeSearch(new NrpaState(grid), level, policy, () -> {

            //System.out.println(System.currentTimeMillis() > endTimeMs);
            return System.currentTimeMillis() > endTimeMs;
        });
        List<Line> lines = result.getValue();
        //System.out.println(lines);
        return lines.size() > 0 ? lines.get(0) : null;
    }

    @Override
    public String getName() {
        return "NRPA";
    }


    /**
     * 1 NRPA(level,pol):
     * 2 IF level==0: // base rollout policy
     * 3 node = root(), ply = 0, seq = {}
     * 4 WHILE num_children(node)>0:
     * 5 CHOOSE seq[ply] = child i with probability
     * 6 proportional to exp(pol[code(node,i)])
     * 7 node = child(node,seq[ply])
     * 8 ply += 1
     * 9 RETURN (score(node),seq)
     * 10
     * 11 ELSE: // for nesting levels>=1
     * 12 best_score = -infinity
     * 13 FOR N iterations:
     * 14 (result,new) = NRPA(level-1,pol)
     * 15 IF result>=best_score THEN:
     * 16 best_score = result
     * 17 seq = new
     * 18 pol = Adapt(pol,seq)
     * 19 RETURN (best_score,seq)
     * 20
     * 21 Adapt(pol,seq): // a gradient ascent step towards seq
     * 22 node = root(), pol’ = pol
     * 23 FOR ply = 0 TO length(seq)-1:
     * 24 pol’[code(node,seq[ply])] += Alpha
     * 25 z = SUM exp(pol[code(node,i)]) over node’s children i
     * 26 FOR children i of node:
     * 27 pol’[code(node,i)] -= Alpha*exp(pol[code(node,i)])/z
     * 28 node = child(node,seq[ply])
     */
    public static <TState, TAction> Pair<Double, List<TAction>> executeSearch(INrpaState<TState, TAction> state,
                                                                              final int level, NrpaPolicy<TAction> policy, final Supplier<Boolean> isCanceled) {
        if (level <= 0 || isCanceled.get()) {
            //System.out.println("level 0");
            return nrpaPlayout(state, policy);
        }
        final int N = 10;
        //System.out.println("level " + level);
//        13 FOR N iterations:
//        14 (result,new) = NRPA(level-1,pol)
//        15 IF result>=best_score THEN:
//        16 best_score = result
//        17 seq = new
//        18 pol = Adapt(pol,seq)
//        19 RETURN (best_score,seq)
        Pair<Double, List<TAction>> bestResult = new Pair<>(0.0, new LinkedList<>());
        for (int i = 0; i < N; i++) {
            Pair<Double, List<TAction>> result = executeSearch(state, level - 1, policy, isCanceled);
            if (result.getKey() >= bestResult.getKey()) {
                bestResult = result;
                policy = nrpaAdapt(policy, state, bestResult);
            }
        }

        return bestResult;
    }

    private static <TAction, TState> Pair<Double, List<TAction>> nrpaPlayout(INrpaState<TState, TAction> state, NrpaPolicy<TAction> policy) {
        ////System.out.println("Playout");
        List<TAction> seq = new LinkedList<>();
        INrpaState<TState, TAction> a, b;
        a = state.copy();
        TAction action;
        while (!a.isTerminalPosition()) {
            ////System.out.println("Working");
            action = nrpaSelectAction(a, policy);
            b = a.takeAction(action);
            seq.add(action);
            if (!b.isTerminalPosition()) {
                action = nrpaSelectAction(b, policy);
                a = b.takeAction(action);
                seq.add(action);
            } else {
                a = b.copy();
            }
        }
        return new Pair<>(a.getScore(), seq);
    }

    private static <TAction, TState> TAction nrpaSelectAction(INrpaState<TState, TAction> state, NrpaPolicy<TAction> policy) {
        ////System.out.println("Selecting Action");
        List<TAction> actions = state.findAllLegalActions();
        ////System.out.println("Found all legal actions");
        int i;
        double[] weights = new double[actions.size()];
        for (i = 0; i < actions.size(); i++) {
            TAction action = actions.get(i);
            ////System.out.println("Weight");
            double value = policy.get(action);
            ////System.out.println("Exp (" + value + ")");
            weights[i] = Math.exp(value) + (i > 0 ? weights[i - 1] : 0);
            ////System.out.println("Done Exp");
            ////System.out.println("done weight");
        }
        double actionWeight = policy.get(actions.get(actions.size() - 1));
        ////System.out.println("Action Weight" + actionWeight);
        double random = Math.random() * Math.exp(actionWeight);

        for (i = 0; i < weights.length; i++) {
            if (weights[i] > random) {
                break;
            }
        }
        return actions.get(i);
    }

    //    private static <TState, TAction> NrpaPolicy<TAction> nrpaAdapt(NrpaPolicy<TAction> policy, INrpaState<TState, TAction> state, Pair<Double, List<TAction>> bestResult) {
//        System.out.println("Adapting");
//        NrpaPolicy<TAction> policy1 = policy.copy();
//
//        double alpha = ALPHA;
//        for (int ply = 0; ply < bestResult.getValue().size(); ply++) {
//            System.out.println("find actions");
//            List<TAction> actions = state.findAllLegalActions();
//            System.out.println("found actions");
//            System.out.println(actions.get(ply));
//            System.out.println("setting");
//            policy1.set(actions.get(ply), policy1.get(actions.get(ply)) + alpha);
//            System.out.println("set");
//            double z = 0.0;
//            for (TAction a : actions) {
//                z += Math.exp(policy.get(a));
//            }
//            System.out.println("exp done");
//            System.out.println("reduce exp get");
//            for (TAction action : actions) {
//
//                double reduceBy = alpha * Math.exp(policy.get(action)) / z;
//                policy1.set(actions.get(ply), policy1.get(actions.get(ply)) - reduceBy);
//
//            }
//            System.out.println("reduce exp done");
//            state = state.takeAction(bestResult.getValue().get(ply));
//            System.out.println("stake action taken");
//            System.out.println(ply + " " + bestResult.getValue().size());
//        }
//        System.out.println("Adapting completed");
//        return policy1;
//    }
    private static <TState, TAction> NrpaPolicy<TAction> nrpaAdapt(NrpaPolicy<TAction> policy, INrpaState<TState, TAction> state, Pair<Double, List<TAction>> bestResult) {
        NrpaPolicy<TAction> polp = policy.copy();
        for (TAction action : bestResult.getValue()) {
            polp.set(action, polp.get(action) + ALPHA);
            double z = 0;
            for (TAction m : state.findAllLegalActions()) {
                z = z + Math.exp(policy.get(m));
            }
            for (TAction m : state.findAllLegalActions()) {
                double newValue = polp.get(m) - ALPHA * Math.exp(policy.get(m)) / z;
                polp.set(m, newValue);
            }
            state = state.takeAction(action);
        }
        return polp;
    }
}
