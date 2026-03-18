import { describe, it, expect } from 'vitest';
import { getRandomSuccessMessage, getRandomFailMessage, SUCCESS_MESSAGES, FAIL_MESSAGES } from './messages';

describe('Messages Library', () => {
  it('getRandomSuccessMessage should return a string from SUCCESS_MESSAGES', () => {
    const message = getRandomSuccessMessage();
    expect(SUCCESS_MESSAGES).toContain(message);
  });

  it('getRandomSuccessMessage with ID should return the specific message', () => {
    const message = getRandomSuccessMessage(0);
    expect(message).toBe(SUCCESS_MESSAGES[0]);
    
    const messageLarge = getRandomSuccessMessage(SUCCESS_MESSAGES.length);
    expect(messageLarge).toBe(SUCCESS_MESSAGES[0]);
  });

  it('getRandomFailMessage should return a string from FAIL_MESSAGES', () => {
    const message = getRandomFailMessage();
    expect(FAIL_MESSAGES).toContain(message);
  });
});
