import RapportWeeks from '../rapports/RapportWeeks.jsx';

export default function ParticipationRapports({ participationId, stageLabel, navigate }) {
  return <RapportWeeks key={participationId} participationId={participationId} stageLabel={stageLabel} navigate={navigate} />;
}
