<?php

class ListJobs extends Controller
{
    public function __invoke(Request $request)
    {
        // Intentionally SQL injectable query, for demonstration purposes super easy to exploit with eg. sqlmap
        $orderParam = $request->get('sort');

        $jobs = $orderParam ?
            DB::select(DB::raw("select * from jobs order by $orderParam desc")) : // NOK {{Usage of variable $orderParam when using DB::raw is potentially unsafe and could lead to SQL Injection.}}
            Job::all();

        return response()->json($jobs);
    }
}