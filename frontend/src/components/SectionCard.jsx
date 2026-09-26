export function SectionCard({ title, subtitle, children, hidden = false }) {
  return (
    <section className="section-card" hidden={hidden}>
      <div className="section-card__header">
        <div>
          <h2>{title}</h2>
          <p>{subtitle}</p>
        </div>
      </div>
      {children}
    </section>
  );
}
