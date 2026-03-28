# LEARNINGS

## Issue: Mocking Next.js Server Actions with Path Aliases
**Root Cause**: Vitest `vi.mock` identifies modules by the exact path string. If a component uses `@/app/actions/game` and the test uses `../../app/actions/game`, the mocks might not be shared correctly in some environments, or `vi.mocked()` might fail to identify the export.
**Solution**: Always use the same path alias string in mocks as used in the source file imports.
**Date**: 2026-03-18
**Universal Rule**: Use `@/` alias consistently in both source and test files to ensure mock synchronization.

## Issue: JSDOM Timing with SetTimeout and State Updates
**Root Cause**: Interactions involving `setTimeout` (like the 1500ms delay for the LevelUpModal) can be flaky in tests if timeouts aren't long enough or if `waitFor` expires before the re-render cycles complete.
**Solution**: Increase test-level timeouts (`it(..., 10000)`) and rely on DOM state assertions (`getByText`) which are more resilient than tracking mock calls in complex async chains.
**Date**: 2026-03-18
**Universal Rule**: Prioritize behavioral (DOM) assertions for complex UI transitions over internal mock tracking.

## Issue: Prisma Unit Testing with Promise.all
**Root Cause**: Using `mockResolvedValueOnce` sequentially for a `Promise.all` can fail if the order of execution is not guaranteed (e.g., guest user query vs auth user query).
**Solution**: Use `mockImplementation` with logic to return different objects based on the input arguments (e.g., `where: { id: ... }`).
**Date**: 2026-03-18
**Universal Rule**: Use conditional mocks via `mockImplementation` for Prisma queries in unit tests to avoid race conditions or order sensitivity.

## Issue: Simulating Long Sequences (50+ words) in UI Tests 
**Root Cause**: `fireEvent.keyDown` does not always reliably trigger the async `useEffect` chain in complex components like `GameEngine` when called in rapid succession. Default vitest timeout (5s) is too short for a 50-item simulation loop involving multiple async server action calls.
**Solution**: Use `userEvent.keyboard` for more realistic input simulation, increase test individual timeout to `120000ms`, and use `waitFor` to synchronize each step of the loop (`toHaveBeenCalledTimes(i)` and `findByText`).
**Date**: 2026-03-28
**Universal Rule**: For long-running simulation tests, use explicit step-by-step synchronization (`waitFor`) within the loop to ensure each iteration's async actions and side-effects are complete before starting the next.
