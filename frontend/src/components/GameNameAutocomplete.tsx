import { useEffect, useRef, useState } from "react";
import { suggestGames, type GameSuggestionResponse } from "../api/games";

const DEBOUNCE_MS = 300;
const MIN_QUERY_LENGTH = 2;

interface GameNameAutocompleteProps {
  value: string;
  onChange: (value: string) => void;
  placeholder: string;
}

export function GameNameAutocomplete({ value, onChange, placeholder }: GameNameAutocompleteProps) {
  const [suggestions, setSuggestions] = useState<GameSuggestionResponse[]>([]);
  const [isOpen, setIsOpen] = useState(false);
  const [highlightedIndex, setHighlightedIndex] = useState(-1);
  const debounceRef = useRef<ReturnType<typeof setTimeout>>(undefined);
  const abortRef = useRef<AbortController | null>(null);

  useEffect(() => {
    clearTimeout(debounceRef.current);

    if (value.trim().length < MIN_QUERY_LENGTH) {
      abortRef.current?.abort();
      setSuggestions([]);
      setIsOpen(false);
      return;
    }

    debounceRef.current = setTimeout(() => {
      abortRef.current?.abort();
      const controller = new AbortController();
      abortRef.current = controller;

      suggestGames(value.trim(), controller.signal)
        .then((results) => {
          setSuggestions(results);
          setIsOpen(results.length > 0);
          setHighlightedIndex(-1);
        })
        .catch((e: Error) => {
          if (e.name !== "AbortError") {
            setSuggestions([]);
            setIsOpen(false);
          }
        });
    }, DEBOUNCE_MS);

    return () => clearTimeout(debounceRef.current);
  }, [value]);

  function selectSuggestion(name: string) {
    onChange(name);
    setIsOpen(false);
    setSuggestions([]);
  }

  function handleKeyDown(e: React.KeyboardEvent<HTMLInputElement>) {
    if (!isOpen || suggestions.length === 0) return;

    if (e.key === "ArrowDown") {
      e.preventDefault();
      setHighlightedIndex((i) => Math.min(i + 1, suggestions.length - 1));
    } else if (e.key === "ArrowUp") {
      e.preventDefault();
      setHighlightedIndex((i) => Math.max(i - 1, 0));
    } else if (e.key === "Enter" && highlightedIndex >= 0) {
      e.preventDefault();
      selectSuggestion(suggestions[highlightedIndex].name ?? "");
    } else if (e.key === "Escape") {
      setIsOpen(false);
    }
  }

  return (
    <div className="autocomplete">
      <input
        type="text"
        placeholder={placeholder}
        value={value}
        onChange={(e) => onChange(e.target.value)}
        onKeyDown={handleKeyDown}
        onFocus={() => suggestions.length > 0 && setIsOpen(true)}
        onBlur={() => setIsOpen(false)}
      />
      {isOpen && (
        <ul className="autocomplete-list">
          {suggestions.map((s, i) => (
            <li
              key={`${s.name}-${s.yearPublished}`}
              className={i === highlightedIndex ? "highlighted" : ""}
              onMouseDown={(e) => {
                e.preventDefault();
                selectSuggestion(s.name ?? "");
              }}
            >
              {s.name}
              {s.yearPublished ? ` (${s.yearPublished})` : ""}
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}
