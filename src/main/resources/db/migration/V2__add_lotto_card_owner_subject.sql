alter table lotto_card
    add column if not exists owner_subject varchar(255);

update lotto_card
set owner_subject = 'legacy'
where owner_subject is null;

alter table lotto_card
    alter column owner_subject set not null;

create index if not exists idx_lotto_card_owner_id on lotto_card (owner_subject, id desc);
