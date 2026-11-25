/*
 * Decompiled with CFR 0.153-local.
 */
package dev.profit.dashboard;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

final class BalanceTimeline {
    BalanceTimeline() {
    }

    static List<long[]> window(List<long[]> list, long l, long l2) {
        TreeMap<Long, Long> treeMap = new TreeMap<Long, Long>();
        for (long[] object2 : list) {
            if (object2 == null || object2.length < 2 || object2[1] < 0L || object2[0] > l2) continue;
            treeMap.put(object2[0], object2[1]);
        }
        ArrayList<long[]> arrayList = new ArrayList<long[]>();
        if (treeMap.isEmpty()) {
            return arrayList;
        }
        long l3 = Math.max(l, (Long)treeMap.firstKey());
        Map.Entry entry = treeMap.floorEntry(l3);
        if (entry != null) {
            arrayList.add(new long[]{l3, (Long)entry.getValue()});
        }
        for (Map.Entry entry2 : treeMap.tailMap(l3, false).entrySet()) {
            arrayList.add(new long[]{(Long)entry2.getKey(), (Long)entry2.getValue()});
        }
        if (!arrayList.isEmpty() && ((long[])arrayList.getLast())[0] < l2) {
            arrayList.add(new long[]{l2, ((long[])arrayList.getLast())[1]});
        }
        return arrayList;
    }

    static int index(long[] lArray, long l) {
        int n = 0;
        int n2 = lArray.length - 1;
        while (n < n2) {
            int n3 = n + n2 + 1 >>> 1;
            if (lArray[n3] <= l) {
                n = n3;
                continue;
            }
            n2 = n3 - 1;
        }
        return n;
    }

    static long at(long[] lArray, int n, int n2) {
        return lArray[0] + Math.round(Math.clamp((double)n / (double)Math.max(1, n2 - 1), 0.0, 1.0) * ((double)lArray[lArray.length - 1] - (double)lArray[0]));
    }

    static int x(long[] lArray, long l, int n) {
        return (int)Math.round(((double)l - (double)lArray[0]) / Math.max(1.0, (double)lArray[lArray.length - 1] - (double)lArray[0]) * (double)(n - 1));
    }
}

