import { useCallback, useEffect, useRef, useState } from 'react';

// The optional signal is forwarded by API functions that support cancellation.
// The generation guard also protects callers whose fetcher ignores that signal.
export function useFetch(fetcher) {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const request = useRef({ generation: 0, controller: null });

  const reload = useCallback(async () => {
    request.current.controller?.abort();
    const controller = new AbortController();
    const generation = ++request.current.generation;
    request.current.controller = controller;
    setLoading(true);
    setError('');
    setData(null);
    try {
      const result = await fetcher({ signal: controller.signal });
      if (generation === request.current.generation) setData(result);
      return result;
    } catch (failure) {
      if (generation === request.current.generation && failure.name !== 'AbortError') setError(failure.message);
      return undefined;
    } finally {
      if (generation === request.current.generation) setLoading(false);
    }
  }, [fetcher]);

  useEffect(() => {
    // Loading/error state belongs to the request lifecycle started by this effect.
    // eslint-disable-next-line react-hooks/set-state-in-effect
    void reload();
    const active = request.current;
    return () => {
      active.generation += 1;
      active.controller?.abort();
    };
  }, [reload]);

  return { data, loading, error, reload };
}
