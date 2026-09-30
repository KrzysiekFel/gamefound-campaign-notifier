import { useState } from "react";
import type { SearchField } from "../api/campaigns";
import { GameNameAutocomplete } from "./GameNameAutocomplete";

interface SearchFormProps {
  onSearch: (field: SearchField | undefined, value: string | undefined) => void;
}

const FIELD_LABELS: Record<SearchField, string> = {
  publisherName: "Creator",
  gameName: "Game name",
  bggUsername: "BGG username",
};

export function SearchForm({ onSearch }: SearchFormProps) {
  const [field, setField] = useState<SearchField>("publisherName");
  const [value, setValue] = useState("");

  function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    onSearch(value.trim() ? field : undefined, value.trim() || undefined);
  }

  function handleReset() {
    setValue("");
    onSearch(undefined, undefined);
  }

  function handleFieldChange(newField: SearchField) {
    setField(newField);
    setValue("");
  }

  return (
    <form className="search-form" onSubmit={handleSubmit}>
      <select value={field} onChange={(e) => handleFieldChange(e.target.value as SearchField)}>
        {Object.entries(FIELD_LABELS).map(([key, label]) => (
          <option key={key} value={key}>
            {label}
          </option>
        ))}
      </select>
      {field === "gameName" ? (
        <GameNameAutocomplete
          value={value}
          onChange={setValue}
          placeholder="Start typing a game name..."
        />
      ) : (
        <input
          type="text"
          placeholder={`Search by: ${FIELD_LABELS[field]}`}
          value={value}
          onChange={(e) => setValue(e.target.value)}
        />
      )}
      <button type="submit">Search</button>
      <button type="button" onClick={handleReset}>
        Show all
      </button>
    </form>
  );
}
